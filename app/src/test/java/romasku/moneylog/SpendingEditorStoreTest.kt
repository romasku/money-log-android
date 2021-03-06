package romasku.moneylog

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import romasku.moneylog.entities.Spending
import romasku.moneylog.lib.NavigateTo
import romasku.moneylog.screens.Route
import romasku.moneylog.screens.spendingEditor.Command
import romasku.moneylog.screens.spendingEditor.Event
import romasku.moneylog.screens.spendingEditor.doCommand
import romasku.moneylog.screens.spendingEditor.init
import romasku.moneylog.screens.spendingEditor.update
import java.math.BigDecimal

class SpendingEditorStoreTest {
    @Test
    fun init_hasNothing() {
        // Stupid test, check that uses defaults in future

        val (initState, cmd) = init()
        assertEquals("", initState.name)
        assertEquals(null, initState.amount)
        assertEquals(null, initState.nameError)
        assertEquals(null, initState.amountError)

        assertEquals(null, cmd)
    }

    @Test
    fun update_nameEntered() {
        // Stupid test, check that uses defaults in future

        val (initState, _) = init()
        val value = "foo"
        val (resState, _) = update(initState, Event.NameEntered(value))
        assertEquals(value, resState.name)
    }

    @Test
    fun update_amountEntered() {
        val (initState, _) = init()
        val value = BigDecimal("200.33")
        val (resState, _) = update(initState, Event.AmountEntered(value))
        assertEquals(value, resState.amount)
    }

    @Test
    fun update_saveRequested_invalid() {
        val (initState, _) = init()
        val (resState, cmd) = update(initState, Event.SaveRequested)
        assertEquals(null, cmd)
        assertNotNull(resState.nameError)
        assertNotNull(resState.amountError)
    }

    @Test
    fun update_saveRequested_valid() {
        var (state, _) = init()
        state = update(state, Event.SaveRequested).first
        state = update(state, Event.NameEntered("foo")).first
        state = update(state, Event.AmountEntered(BigDecimal("200.33"))).first
        val (resState, cmd) = update(state, Event.SaveRequested)
        assertEquals(Command.SaveSpending("foo", BigDecimal("200.33")), cmd)
        assertEquals(null, resState.nameError)
        assertEquals(null, resState.amountError)
    }

    @Test
    fun command_saveSpending() {
        val name = "testName"
        val amount = BigDecimal("200.33")
        val commandRun = doCommand.testRun(Command.SaveSpending(name, amount))
        commandRun.assertCommandStep(StoreSpending(name, amount), Spending("1", name, amount))
        commandRun.assertCommandStep(NavigateTo(Route.SpendingList), Unit)
        commandRun.assertCompleted()
    }
}
