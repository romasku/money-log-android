package romasku.moneylog.lib

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

typealias Generator<P, R> = suspend GeneratorContext<P, R>.() -> Unit

interface GeneratorContext<P, R> {
    suspend fun yield(value: R): P
}

interface RunningGenerator<in P, out R> {
    val lastResult: R?
    fun proceed(value: P): R?
}

class GeneratorFinished : Throwable()

fun <P, R> startGenerator(block: Generator<P, R>): RunningGenerator<P, R> = GeneratorRunner(block)

internal class GeneratorRunner<P, R>(block: Generator<P, R>) :
    GeneratorContext<P, R>,
    RunningGenerator<P, R>,
    Continuation<Unit> {
    override var lastResult: R? = null
        private set
    private var continuation: Continuation<P>? = null
    private var exception: Throwable? = null

    init {
        block.createCoroutine(this, this).resume(Unit)
        exception?.let { throw it }
    }

    override suspend fun yield(value: R): P = suspendCoroutineUninterceptedOrReturn {
        lastResult = value
        continuation = it
        COROUTINE_SUSPENDED
    }

    override fun proceed(value: P): R? {
        continuation?.resume(value) ?: run {
            throw GeneratorFinished()
        }
        exception?.let { throw it }
        return this.lastResult
    }

    override val context: CoroutineContext get() = EmptyCoroutineContext
    override fun resumeWith(result: Result<Unit>) {
        continuation = null
        result
            .onSuccess { lastResult = null }
            .onFailure { exception = it }
    }
}
