package romasku.moneylog.lib

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotSame
import org.junit.Test
import romasku.moneylog.CommandExecutor
import romasku.moneylog.cmdCombo
import romasku.moneylog.cmdPlus1
import romasku.moneylog.cmdPlus2
import romasku.moneylog.expectError

class TryTest {

    @Test
    fun `test try`() {
        val executor = CommandExecutor(emptyList())
        executor.launch(cmdCombo())
    }

}
