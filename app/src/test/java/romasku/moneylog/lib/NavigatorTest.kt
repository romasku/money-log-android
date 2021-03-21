package romasku.moneylog.lib

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import romasku.moneylog.expectError

private sealed class Route {
    object A : Route()
    object B : Route()
}

private sealed class Screen<T>(val value: T) {
    class ScreenA<T>(value: T) : Screen<T>(value)
    class ScreenB<T>(value: T) : Screen<T>(value)
}

private val <BS>Navigator<BS, *>.topScreen: BS
    get() = this.current.last()

@Suppress("UNCHECKED_CAST")
private fun <T> Screen<Effector>.effect(effect: Effect<T>, callback: (T) -> Unit = {}) {
    (value { _, _ -> })(effect) { callback(it as T) }
}

private fun Screen<Effector>.navigate(route: Route) {
    effect(NavigateTo(route))
}

class NavigatorTest {
    private fun makeTestNavigator() = Navigator(
        effector = { it },
        routing = {
            it: Route, effector: Effector ->
            when (it) {
                Route.A -> Screen.ScreenA(effector)
                Route.B -> Screen.ScreenB(effector)
            }
        },
        Route.A,
    )

    @Test
    fun `test navigator initial route`() {
        val navigator = makeTestNavigator()
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertEquals(1, navigator.current.size)
    }

    @Test
    fun `test navigate to new page`() {
        val navigator = makeTestNavigator()
        navigator.topScreen.navigate(Route.B)
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertTrue(navigator.current[1] is Screen.ScreenB<*>)
        assertEquals(2, navigator.current.size)
    }

    @Test
    fun `test go back`() {
        val navigator = makeTestNavigator()
        navigator.topScreen.navigate(Route.B)
        navigator.dispatchBack()
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertEquals(1, navigator.current.size)
    }

    @Test
    fun `test go back from effector`() {
        val navigator = makeTestNavigator()
        navigator.topScreen.navigate(Route.B)
        navigator.topScreen.effect(NavigateBack)
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertEquals(1, navigator.current.size)
    }

    @Test
    fun `test start for result`() {
        val navigator = makeTestNavigator()
        var result: Any? = null
        navigator.topScreen.effect(StartForResult(Route.B)) { result = it }
        assertTrue(navigator.current[1] is Screen.ScreenB<*>)
        navigator.topScreen.effect(SetResult(42))
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertEquals(1, navigator.current.size)
        assertEquals(42, result)
    }

    @Test
    fun `exception for non top navigation`() {
        val navigator = makeTestNavigator()
        navigator.topScreen.navigate(Route.B)
        expectError(Throwable::class) {
            navigator.current[0].navigate(Route.B)
        }
    }
}
