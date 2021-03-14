package romasku.moneylog.lib

import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.RestrictsSuspension

open class Effect<out T>

object Noop : Effect<Unit>()
data class Dispatch<M>(val message: M) : Effect<Unit>()

typealias SimpleEffector = (Effect<*>) -> Any

typealias Effector = (SimpleEffector) -> (SimpleEffector)

inline fun <T, reified E : Effect<T>> makeEffector(crossinline process: (E) -> T): Effector = {
    next ->
    { effect: Effect<*> ->
        if (effect is E) {
            process(effect) as Any
        } else {
            next(effect)
        }
    }
}

operator fun Effector.plus(another: Effector): Effector = { this(another(it)) }

val noop = makeEffector { _: Noop -> }

fun <M> makeDispatch(store: Store<*, M, *>) = makeEffector {
    it: Dispatch<M> ->
    store.dispatch(it.message)
}

class UnknownEffect(val effect: Effect<*>) : Throwable()

@RestrictsSuspension
interface CommandCtx {
    suspend fun <T> effect(effect: Effect<T>): T
}

typealias StoreInit<S, C> = () -> Pair<S, C?>
typealias StoreUpdate<S, C, M> = (S, M) -> Pair<S, C?>
typealias StoreDoCommand<C> =  (C) -> Generator<*, Effect<*>>

fun <S, C>defInit(init: () -> Pair<S, C?>): StoreInit<S, C> = init
fun <S, C, M>defUpdate( update: (S, M) -> Pair<S, C?>): StoreUpdate<S, C, M> = update
fun <C>defDoCommand(doCommand: suspend CommandCtx.(C) -> Unit): StoreDoCommand<C> = {
    cmd: C -> {
        val commandCtx: CommandCtx = object: CommandCtx {
            @Suppress("UNCHECKED_CAST")
            override suspend fun <T> effect(effect: Effect<T>): T  = yield(effect) as T
        }
        doCommand.invoke(commandCtx, cmd)
    }
}

class Store<S, M, C> (
    init: StoreInit<S, C>,
    val update: StoreUpdate<S, C, M>,
    val doCommand: StoreDoCommand<C>,
    effector: Effector = { it }
) {
    interface Subscription {
        fun unsubscribe()
    }

    private val applyEffect = (effector + noop + makeDispatch(this)) { throw UnknownEffect(it) }
    private val state: AtomicReference<S>
    private val subscriptions: MutableList<(S) -> Unit> = mutableListOf()

    init {
        val(initState, initCommand) = init()
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
        val generator = startGenerator<Any, Effect<*>>(doCommand(cmd))
        while(true) {
            generator.lastResult?.also {
                generator.proceed(applyEffect(it))
            } ?: break
        }
    }
}

interface StoreDefs<S, E, C> {
    val init: () -> Pair<S, C?>
    val update: (S, E) -> Pair<S, C?>
    val doCommand: (C) -> Generator<*, Effect<*>>

    fun toStore(effector: Effector) = Store(init, update, doCommand, effector)
}