package romasku.moneylog.lib

import java.util.concurrent.atomic.AtomicReference

// Effect handling

open class Effect<out T>

data class Dispatch<M>(val message: M) : Effect<Unit>()

typealias SimpleEffector = (Effect<*>, (Any) -> Unit) -> Any

typealias Effector = (SimpleEffector) -> (SimpleEffector)

inline fun <T, reified E : Effect<T>> makeEffector(crossinline process: (E) -> T): Effector =
    { next: SimpleEffector ->
        { effect: Effect<*>, setResult ->
            if (effect is E) {
                setResult(process(effect) as Any)
            } else {
                next(effect, setResult)
            }
        }
    }

inline fun <T : Any, reified E : Effect<T>> makeAsyncEffector(crossinline process: (E, (T) -> Unit) -> Unit): Effector =
    { next: SimpleEffector ->
        { effect: Effect<*>, setResult ->
            if (effect is E) {
                process(effect, setResult)
            } else {
                next(effect, setResult)
            }
        }
    }

fun makeTapEffector(sideEffect: (Effect<*>) -> Unit): Effector = { next ->
    { effect, callback ->
        sideEffect(effect)
        next(effect, callback)
    }
}

operator fun Effector.plus(another: Effector): Effector = { this(another(it)) }

class UnknownEffect(val effect: Effect<*>) : Throwable()

interface CommandCtx {
    suspend fun <T> effect(effect: Effect<T>): T
}

// Def helpers

typealias StoreInit<S, C> = () -> Pair<S, C?>
typealias StoreUpdate<S, C, M> = (S, M) -> Pair<S, C?>
typealias StoreDoCommand<C> = (C) -> Generator<*, Effect<*>, Unit>

fun <S, C> defInit(init: () -> Pair<S, C?>): StoreInit<S, C> = init
fun <S, C, M> defUpdate(update: (S, M) -> Pair<S, C?>): StoreUpdate<S, C, M> = update
fun <C> defDoCommand(doCommand: suspend CommandCtx.(C) -> Unit): StoreDoCommand<C> = { cmd: C ->
    {
        val commandCtx: CommandCtx = object : CommandCtx {
            @Suppress("UNCHECKED_CAST")
            override suspend fun <T> effect(effect: Effect<T>): T = yield(effect) as T
        }
        doCommand.invoke(commandCtx, cmd)
    }
}

// Store

class Store<S, M, C>(
    init: StoreInit<S, C>,
    val update: StoreUpdate<S, C, M>,
    val doCommand: StoreDoCommand<C>,
    effector: Effector = { it }
) {
    interface Subscription {
        fun unsubscribe()
    }

    val current: S
        get() = state.get()

    private val applyEffect = (
        effector + makeEffector { it: Dispatch<M> ->
            dispatch(it.message)
        }
        ) { effect, _ -> throw UnknownEffect(effect) }
    private val state: AtomicReference<S>
    private val subscriptions: MutableList<(S) -> Unit> = mutableListOf()

    init {
        val (initState, initCommand) = init()
        state = AtomicReference(initState)
        runCommand(initCommand)
    }

    fun dispatch(message: M) {
        var command: C?
        do {
            val oldState = state.get()
            val (newState, newCommand) = update(oldState, message)
            command = newCommand
        } while (!state.compareAndSet(oldState, newState))
        subscriptions.forEach { it(state.get()) }
        runCommand(command)
    }

    fun subscribe(listener: (S) -> Unit): Subscription {
        subscriptions.add(listener)
        listener(state.get())
        return object : Subscription {
            override fun unsubscribe() {
                subscriptions.remove(listener)
            }
        }
    }

    private fun runCommand(cmd: C?) {
        if (cmd == null) {
            return
        }
        val generator = startGenerator<Any, Effect<*>, Unit>(doCommand(cmd))
        lateinit var proceed: ((Any) -> Unit)
        val runNext = {
            generator.lastResult?.also { effect ->
                applyEffect(effect) {
                    proceed(it)
                }
            }
        }
        proceed = { it: Any ->
            generator.proceed(it)
            runNext()
        }
        runNext()
    }
}
