package romasku.moneylog.lib

import junit.framework.TestCase.assertEquals
import org.junit.Test
import romasku.moneylog.expectError

class GeneratorTest {

    @Test
    fun `test simple generator`() {
        val runningGenerator = startGenerator<Unit, Int> {
            for (x in 1..10) {
                yield(x)
            }
        }
        for (x in 1..10) {
            assertEquals(x, runningGenerator.lastResult)
            runningGenerator.proceed(Unit)
        }
        assertEquals(null, runningGenerator.lastResult)
    }

    @Test
    fun `test passing data to generator`() {
        val runningGenerator = startGenerator<Int, Int> {
            var y = 0
            while (y != 42) {
                y = yield(y)
            }
        }
        assertEquals(0, runningGenerator.lastResult)
        runningGenerator.proceed(2)
        assertEquals(2, runningGenerator.lastResult)
        runningGenerator.proceed(100)
        assertEquals(100, runningGenerator.lastResult)
        runningGenerator.proceed(42)
        assertEquals(null, runningGenerator.lastResult)
    }

    @Test
    fun `test failing at start generator`() {
        val throwable = expectError(Throwable::class) {
            startGenerator<Int, Int> {
                throw Throwable("test")
            }
        }
        assertEquals("test", throwable.message)
    }

    @Test
    fun `test failing at value generator`() {
        val runningGenerator = startGenerator<Int, Int> {
            var y = 0
            while (y != 42) {
                y = yield(y)
            }
            throw Throwable("test")
        }
        assertEquals(0, runningGenerator.lastResult)
        val throwable = expectError(Throwable::class) {
            runningGenerator.proceed(42)
        }
        assertEquals("test", throwable.message)
    }

    @Test
    fun `proceed after finish validated`() {
        val runningGenerator = startGenerator<Int, Int> {}
        assertEquals(null, runningGenerator.lastResult)
        expectError(GeneratorFinished::class) {
            runningGenerator.proceed(42)
        }
    }
}
