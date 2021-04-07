package romasku.moneylog

import junit.framework.TestCase.assertEquals
import romasku.moneylog.lib.Effect
import romasku.moneylog.lib.RunningGenerator
import romasku.moneylog.lib.StoreDoCommand
import kotlin.reflect.KClass

class CommandTestRun(private val generator: RunningGenerator<Any, Effect<*>, Unit>) {
    fun <T> assertCommandStep(effect: Effect<T>, result: T) {
        assertEquals(effect, generator.lastResult)
        generator.proceed(result as Any)
    }
    fun assertCompleted() {
        assertEquals(null, generator.lastResult)
    }
}

fun <C> StoreDoCommand<C>.testRun(cmd: C) = CommandTestRun(
    romasku.moneylog.lib.startGenerator(
        this(
            cmd
        )
    )
)

@Suppress("UNCHECKED_CAST")
fun <T : Throwable> expectError(type: KClass<T>, block: () -> Unit): T {
    try {
        block()
    } catch (e: Throwable) {
        if (type.isInstance(e)) {
            return e as T
        }
    }
    throw Throwable("Exception not thrown")
}
