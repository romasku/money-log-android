package romasku.moneylog.lib

private sealed class Event<R> {
    class Back<R> : Event<R>()
    class GoTo<R>(val route: R) : Event<R>()
}

// Navigation effect:
data class NavigateTo<R>(val route: R) : Effect<Unit>()
object NavigateBack : Effect<Unit>()

class Navigator<BS, R>(
    effector: Effector,
    private val routing: (R, Effector) -> BS,
    initialRoute: R
) {
    private val effector = effector +
            makeEffector { it: NavigateTo<R> -> store.dispatch(Event.GoTo(it.route)) } +
            makeEffector { _: NavigateBack -> store.dispatch(Event.Back()) }

    private val store = Store<List<BS>, Event<R>, Unit>(
        init = defInit { Pair(listOf(setupNewStore(initialRoute)), null) },
        update = defUpdate { stack, event ->
            when (event) {
                is Event.GoTo<R> -> Pair(stack + listOf(setupNewStore(event.route)), null)
                is Event.Back -> Pair(stack.subList(0, Math.max(stack.size - 1, 1)), null)
            }
        },
        doCommand = defDoCommand { }
    )

    val current by store::current

    fun subscribe(callback: (List<BS>) -> Unit) = store.subscribe(callback)

    fun dispatchBack() = store.dispatch(Event.Back())

    private fun setupNewStore(route: R): BS = routing(route, effector)
}
