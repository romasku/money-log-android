package romasku.moneylog.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import romasku.moneylog.screens.Screen.SpendingEditor
import romasku.moneylog.screens.Screen.SpendingsList
import romasku.moneylog.screens.Screen.Statistics
import romasku.moneylog.screens.spendingEditor.makeSpendingEditorView
import romasku.moneylog.screens.spendingsList.makeSpendingsListView
import romasku.moneylog.screens.statistics.makeStatisticsView

fun viewSelector(inflater: LayoutInflater, parent: ViewGroup) = { screen: Screen ->
    when (screen) {
        is SpendingEditor -> makeSpendingEditorView(screen.store, inflater, parent)
        is SpendingsList -> makeSpendingsListView(screen.store, inflater, parent)
        is Statistics -> makeStatisticsView(screen.store, inflater, parent)
    }
}
