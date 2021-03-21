package romasku.moneylog.screens

import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.Navigator
import romasku.moneylog.screens.spendingEditor.SpendingEditorStore
import romasku.moneylog.screens.spendingEditor.makeSpendingEditorStore
import romasku.moneylog.screens.spendingsList.SpendingsListStore
import romasku.moneylog.screens.spendingsList.makeSpendingsListStore

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
        is Route.NewSpending -> Screen.SpendingEditor(makeSpendingEditorStore(effector))
        is Route.SpendingList -> Screen.SpendingsList(makeSpendingsListStore(effector))
    }
}

fun makeNavigator(effector: Effector) =
    Navigator<Screen, Route>(effector, routing, Route.NewSpending)
