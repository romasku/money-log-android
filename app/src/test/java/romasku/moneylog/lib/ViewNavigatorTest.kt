package romasku.moneylog.lib

import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.random.Random

class ViewNavigatorTest {

    @Test
    fun `test make view for initial route`() {
        val viewNavigator = ViewNavigator(listOf(1), {}, { it * 2 })
        assertEquals(2, viewNavigator.view)
    }

    @Test
    fun `test navigate to new page`() {
        var callback: (List<Int>) -> Unit = {}
        val viewNavigator = ViewNavigator(listOf(1), { callback = it }, { it * 2 })
        callback(listOf(1, 2))
        assertEquals(4, viewNavigator.view)
    }

    @Test
    fun `test navigate to back reuses view`() {
        var callback: (List<Int>) -> Unit = {}
        val viewNavigator = ViewNavigator(listOf(1), { callback = it }, { Random.nextDouble() })
        val initialView = viewNavigator.view
        callback(listOf(1, 2))
        callback(listOf(1))
        assertEquals(initialView, viewNavigator.view)
    }

    @Test
    fun `test cleans cache`() {
        var callback: (List<Int>) -> Unit = {}
        val viewNavigator = ViewNavigator(listOf(1), { callback = it }, { Random.nextDouble() })
        callback(listOf(1, 2))
        callback(listOf(1))
        assertEquals(1, viewNavigator.viewCache.size)
    }

    @Test
    fun `test subscription`() {
        var callback: (List<Int>) -> Unit = {}
        val viewNavigator = ViewNavigator(listOf(1), { callback = it }, { it * 2 })
        var fromSubscription = -1
        val subscription = viewNavigator.subscribe { fromSubscription = it }
        assertEquals(2, fromSubscription)
        callback(listOf(1, 2))
        assertEquals(4, fromSubscription)
        callback(listOf(1))
        assertEquals(2, fromSubscription)
        subscription.unsubscribe()
        callback(listOf(1, 2))
        assertEquals(2, fromSubscription)
    }
}
