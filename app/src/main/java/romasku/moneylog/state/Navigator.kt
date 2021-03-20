package romasku.moneylog.state

import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.Navigator

sealed class Screen {
    class SpendingEditor(val store: SpendingEditorStore) : Screen()
    class SpendingsList(val store: SpendingsListStore) : Screen()
}

sealed class Route {
    object NewSpending : Route()
    object SpendingList : Route()
}

internal val routing = { route: Route, effector: Effector ->
    when (route) {
        is Route.NewSpending -> Screen.SpendingEditor(SpendingEditor.toStore(effector))
        is Route.SpendingList -> Screen.SpendingsList(SpendingsList.toStore(effector))
    }
}

fun makeNavigator(effector: Effector) = Navigator<Screen, Route>(effector, routing, Route.NewSpending)
