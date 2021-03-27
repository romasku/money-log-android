package romasku.moneylog.screens

import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.Navigator
import romasku.moneylog.screens.spendingEditor.SpendingEditorStore
import romasku.moneylog.screens.spendingEditor.makeSpendingEditorStore
import romasku.moneylog.screens.spendingsList.SpendingsListStore
import romasku.moneylog.screens.spendingsList.makeSpendingsListStore
import romasku.moneylog.screens.statistics.StatisticsStore
import romasku.moneylog.screens.statistics.makeStatisticsStore

sealed class Screen {
    class SpendingEditor(val store: SpendingEditorStore) : Screen()
    class SpendingsList(val store: SpendingsListStore) : Screen()
    class Statistics(val store: StatisticsStore) : Screen()
}

sealed class Route {
    object NewSpending : Route()
    object SpendingList : Route()
    object Statistics : Route()
}

internal val routing = { route: Route, effector: Effector ->
    when (route) {
        is Route.NewSpending -> Screen.SpendingEditor(makeSpendingEditorStore(effector))
        is Route.SpendingList -> Screen.SpendingsList(makeSpendingsListStore(effector))
        is Route.Statistics -> Screen.Statistics(makeStatisticsStore(effector))
    }
}

fun makeNavigator(effector: Effector) =
    Navigator<Screen, Route>(effector, routing, Route.SpendingList)
