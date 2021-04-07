package romasku.moneylog.lib2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import romasku.moneylog.lib.Generator
import romasku.moneylog.lib.startGenerator
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

interface Effect<out T>

interface RunningCommandInfo {
    val command: Command<*>
    val parentInfo: RunningCommandInfo?
}

interface CommandCtx : RunningCommandInfo {
    suspend fun <T> effect(effect: Effect<T>): T
}

typealias CancelEffect = () -> Unit

interface Effector<E : Effect<R>, R> {
    fun handlesEffect(effect: Effect<*>): Boolean

    fun process(caller: RunningCommandInfo, effect: E, callback: (R) -> Unit): CancelEffect
}

suspend fun <E : Effect<R>, R> Effector<E, R>.run(caller: RunningCommandInfo, effect: E) =
    suspendCancellableCoroutine<R> { continuation ->
        val cancel = process(caller, effect) {
            continuation.resume(it)
        }
        continuation.invokeOnCancellation { cancel() }
    }

inline fun <reified E : Effect<R>, R> effector(crossinline block: (E) -> R): Effector<E, R> {
    return object : Effector<E, R> {
        override fun handlesEffect(effect: Effect<*>) = effect is E
        override fun process(
            caller: RunningCommandInfo,
            effect: E,
            callback: (R) -> Unit
        ): CancelEffect {
            callback(block(effect))
            return { }
        }
    }
}


data class RunningCommand(
    val generator: Generator<*, Effect<*>, *>,
    override val command: Command<*>,
    override val parentInfo: RunningCommandInfo?
) : RunningCommandInfo

class CommandExecutor(effectors: List<Effector<*, *>>) {

    private val scope = CoroutineScope(EmptyCoroutineContext)
    private val allEffectors: List<Effector<*, *>>

    init {
        val commandEffector = object : Effector<Command<*>, Any?> {
            override fun handlesEffect(effect: Effect<*>) = effect is Command
            override fun process(
                caller: RunningCommandInfo,
                effect: Command<*>,
                callback: (Any?) -> Unit
            ): CancelEffect {
                val running = prepareCommand(effect, caller)
                val job = scope.launch {
                    callback(run(running))
                }
                return { job.cancel() }
            }
        }
        allEffectors = (effectors + listOf(commandEffector))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Effect<R>, R> getEffector(effect: T): Effector<T, R> {
        return allEffectors.first { it.handlesEffect(effect) } as Effector<T, R>
    }

    private fun prepareCommand(cmd: Command<*>, parentInfo: RunningCommandInfo?) = RunningCommand(
        generator = {
            val ctx = object : CommandCtx {
                override val command = cmd
                override val parentInfo = parentInfo

                @Suppress("UNCHECKED_CAST")
                override suspend fun <T> effect(effect: Effect<T>) = yield(effect) as T
            }
            ctx.(cmd.run)()
        },
        command = cmd,
        parentInfo = parentInfo
    )

    private suspend fun run(runningCommand: RunningCommand): Any? {
        val generator = startGenerator<Any?, Effect<*>, Any?>(runningCommand.generator)
        while (true) {
            val effect = generator.lastResult ?: break
            val effector = getEffector(effect)
            val result = effector.run(runningCommand, effect)
            generator.proceed(result)
        }
        return generator.exitValue
    }

    fun launch(cmd: Command<*>) {
        val running = prepareCommand(cmd, null)
        runBlocking(scope.coroutineContext) { run(running) }
    }
}

typealias CommandBlock<R> = suspend CommandCtx.() -> R

interface Command<R> : Effect<R> {
    val run: CommandBlock<R>
}

fun <R> command(block: CommandBlock<R>): () -> Command<R> {
    val identity = Any()

    data class Command0(val id: Any) : Command<R> {
        override val run: CommandBlock<R> = {
            block()
        }
    }
    return {
        Command0(identity)
    }
}

fun <T1, R> command(block: suspend CommandCtx.(T1) -> R): (T1) -> Command<R> {
    val identity = Any()

    data class Command1(val arg1: T1, val id: Any) : Command<R> {
        override val run: CommandBlock<R> = {
            block(arg1)
        }
    }
    return {
        Command1(it, identity)
    }
}

fun <T1, T2, R> command(block: suspend CommandCtx.(T1, T2) -> R): (T1, T2) -> Command<R> {
    val identity = Any()

    data class Command1(val arg1: T1, val arg2: T2, val id: Any) : Command<R> {
        override val run: CommandBlock<R> = {
            block(arg1, arg2)
        }
    }
    return { arg1, arg2 ->
        Command1(arg1, arg2, identity)
    }
}

// Channels

//interface ChannelEffect
//
//fun <T>putToChannel(val channel: Channel<T>, val value: T) = command {
//    channel:
//}
//
//@Suppress("ClassName")
//data class putToChannel<T>(val channel: Channel<T>, val value: T): Effect<Unit>, ChannelEffect
//
//@Suppress("ClassName")
//data class takeFromChannel<T>(val channel: Channel<T>): Effect<T>, ChannelEffect
