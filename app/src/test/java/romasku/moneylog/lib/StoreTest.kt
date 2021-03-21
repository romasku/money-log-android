package romasku.moneylog.lib

import junit.framework.TestCase.assertEquals
import org.junit.Test

class StoreTest {

    @Test
    fun `dispatch sends events after update`() {
        val store = Store<Int, Int, Unit>(
            init = { Pair(20, null) },
            update = { state, event -> Pair(state + event, null) },
            doCommand = defDoCommand {},
        )
        var fromSubscription: Int? = null
        store.subscribe { fromSubscription = it }
        assertEquals(20, fromSubscription)
        assertEquals(20, store.current)
        store.dispatch(22)
        assertEquals(42, fromSubscription)
        assertEquals(42, store.current)
        store.dispatch(-42)
        assertEquals(0, fromSubscription)
        assertEquals(0, store.current)
    }

    @Test
    fun `unsubscribe stops events`() {
        val store = Store<Int, Int, Unit>(
            init = { Pair(20, null) },
            update = { state, event -> Pair(state + event, null) },
            doCommand = defDoCommand {},
        )
        var fromSubscription: Int? = null
        val subscription = store.subscribe { fromSubscription = it }
        assertEquals(20, fromSubscription)
        assertEquals(20, store.current)
        store.dispatch(22)
        assertEquals(42, fromSubscription)
        assertEquals(42, store.current)
        subscription.unsubscribe()
        store.dispatch(-42)
        assertEquals(42, fromSubscription)
        assertEquals(0, store.current)
    }

    @Test
    fun `test running commands`() {

        class IntEffect(val value: Int) : Effect<Int>()
        class StringEffect(val value: String) : Effect<String>()

        class TestCommand

        val store = Store<String, String, TestCommand>(
            init = { Pair(String(), TestCommand()) },
            update = { _, event -> Pair(event, null) },
            doCommand = defDoCommand {
                val int = effect(IntEffect(20))
                val str = effect(StringEffect("foo"))
                effect(Dispatch(int.toString() + str))
            },
            effector = makeEffector { it: IntEffect -> it.value } +
                makeEffector { it: StringEffect -> it.value }
        )

        assertEquals("20foo", store.current)
    }
}
