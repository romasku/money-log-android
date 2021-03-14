package romasku.moneylog.state

import romasku.moneylog.lib.*

typealias NavigatorStore = Store<NavigatorState, Route, Unit>

sealed class Route

object NewSpending : Route()
object SpendingList : Route()

sealed class NavigatorState

data class SpendingEditorScreen(val store: SpendingEditorStore) : NavigatorState()

data class NavigateTo(val route: Route): Effect<Unit>()

fun makeNavigator(effector: Effector): NavigatorStore {
    var store: NavigatorStore? = null

    val route = makeEffector { it: NavigateTo ->
        store?.dispatch(it.route) ?: throw Exception("Tried to navigate before navigator was initialized")
    }

    val effectorWithRoute = effector + route

    fun init() = Pair(SpendingEditorScreen(SpendingEditor.toStore(effectorWithRoute)), null)

    fun reduce(state: NavigatorState, event: Route): Pair<NavigatorState, Unit?> {
        return when (event) {
            is NewSpending -> Pair(
                SpendingEditorScreen(SpendingEditor.toStore(effectorWithRoute)),
                null
            )
            is SpendingList -> Pair(state, null)
        }
    }

    store = Store(::init, ::reduce, defDoCommand {}, effectorWithRoute)

    return store
}
