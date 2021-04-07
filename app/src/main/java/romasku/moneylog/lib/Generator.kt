package romasku.moneylog.lib

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

typealias Generator<P, O, R> = suspend GeneratorContext<P, O>.() -> R

interface GeneratorContext<P, R> {
    suspend fun yield(value: R): P
}

interface RunningGenerator<in P, out O, out R> {
    val lastResult: O?
    val exitValue: R?
    fun proceed(value: P): O?
}

class GeneratorFinished : Throwable()

fun <P, O, R> startGenerator(block: Generator<P, O, R>): RunningGenerator<P, O, R> = GeneratorRunner(block)

internal class GeneratorRunner<P, O, R>(block: Generator<P, O, R>) :
    GeneratorContext<P, O>,
    RunningGenerator<P, O, R>,
    Continuation<R> {
    override var lastResult: O? = null
        private set
    override var exitValue: R? = null
        private set
    private var continuation: Continuation<P>? = null
    private var exception: Throwable? = null

    init {
        block.createCoroutine(this, this).resume(Unit)
        exception?.let { throw it }
    }

    override suspend fun yield(value: O): P = suspendCoroutineUninterceptedOrReturn {
        lastResult = value
        continuation = it
        COROUTINE_SUSPENDED
    }

    override fun proceed(value: P): O? {
        continuation?.resume(value) ?: run {
            throw GeneratorFinished()
        }
        exception?.let { throw it }
        return this.lastResult
    }

    override val context: CoroutineContext get() = EmptyCoroutineContext
    override fun resumeWith(result: Result<R>) {
        continuation = null
        result
            .onSuccess {
                lastResult = null
                exitValue = it
            }
            .onFailure { exception = it }
    }
}
