package romasku.moneylog.lib

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

private sealed class Route {
    object A : Route()
    object B : Route()
}

private sealed class Screen<T>(val value: T) {
    class ScreenA<T>(value: T) : Screen<T>(value)
    class ScreenB<T>(value: T) : Screen<T>(value)
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

    private fun navigate(screen: Screen<Effector>, route: Route) {
        (screen.value { it })(NavigateTo(route))
    }

    @Test
    fun `test navigator initial route`() {
        val navigator = makeTestNavigator()
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertEquals(1, navigator.current.size)
    }

    @Test
    fun `test navigate to new page`() {
        val navigator = makeTestNavigator()
        val screen: Screen<Effector> = navigator.current.last()
        navigate(screen, Route.B)
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertTrue(navigator.current[1] is Screen.ScreenB<*>)
        assertEquals(2, navigator.current.size)
    }

    @Test
    fun `test go back`() {
        val navigator = makeTestNavigator()
        val screen: Screen<Effector> = navigator.current.last()
        navigate(screen, Route.B)
        navigator.dispatchBack()
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertEquals(1, navigator.current.size)
    }

    @Test
    fun `test go back from effector`() {
        val navigator = makeTestNavigator()
        val screen = navigator.current.last()
        navigate(screen, Route.B)
        (screen.value { it })(NavigateBack)
        assertTrue(navigator.current[0] is Screen.ScreenA<*>)
        assertEquals(1, navigator.current.size)
    }
}
