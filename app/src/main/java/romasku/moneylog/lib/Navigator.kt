package romasku.moneylog.lib

import romasku.moneylog.lib.Event.Back
import romasku.moneylog.lib.Event.ForResult
import romasku.moneylog.lib.Event.GoTo

private sealed class Event<R> {
    class Back<R> : Event<R>()
    class GoTo<R>(val route: R) : Event<R>()
    class ForResult<R>(val route: R, val setResult: (Any) -> Unit) : Event<R>()
}

sealed interface NavigationEffect

data class NavigateTo<R>(val route: R) : Effect<Unit>(), NavigationEffect
object NavigateBack : Effect<Unit>(), NavigationEffect
data class StartForResult<R>(val route: R) : Effect<Any>(), NavigationEffect
data class SetResult(val result: Any) : Effect<Unit>(), NavigationEffect

class Navigator<BS, R>(
    private val effector: Effector,
    private val routing: (R, Effector) -> BS,
    initialRoute: R
) {

    private val store = Store<List<BS>, Event<R>, Unit>(
        init = defInit { Pair(listOf(setupNewStore(initialRoute)), null) },
        update = defUpdate { stack, event ->
            when (event) {
                is GoTo -> Pair(stack + listOf(setupNewStore(event.route)), null)
                is ForResult -> Pair(stack + listOf(setupNewStore(event.route, event.setResult)), null)
                is Back -> Pair(stack.subList(0, Math.max(stack.size - 1, 1)), null)
            }
        },
        doCommand = defDoCommand { }
    )

    val current by store::current

    fun subscribe(callback: (List<BS>) -> Unit) = store.subscribe(callback)

    fun dispatchBack() = store.dispatch(Back())

    private fun setupNewStore(route: R, setResult: ((Any) -> Unit)? = null): BS {
        var viewStore: BS? = null

        var effectorForRoute = effector +
            makeTapEffector { effect ->
                if (effect is NavigationEffect && current.last() != viewStore) {
                    throw Throwable("Tried to use navigation effects when not at the top of the stack")
                }
            } +
            makeEffector { it: NavigateTo<R> -> store.dispatch(GoTo(it.route)) } +
            makeEffector { _: NavigateBack -> store.dispatch(Back()) } +
            makeAsyncEffector { effect: StartForResult<R>, callback ->
                store.dispatch(ForResult(effect.route, callback))
            }
        if (setResult != null) {
            effectorForRoute += makeEffector { it: SetResult ->
                setResult(it.result)
                store.dispatch(Back())
            }
        }
        viewStore = routing(route, effectorForRoute)
        return viewStore
    }
}
