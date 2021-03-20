package romasku.moneylog

import junit.framework.TestCase.assertEquals
import romasku.moneylog.lib.Effect
import romasku.moneylog.lib.RunningGenerator
import romasku.moneylog.lib.StoreDoCommand

class CommandTestRun(private val generator: RunningGenerator<Any, Effect<*>>) {
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
