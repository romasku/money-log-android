package romasku.moneylog.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import romasku.moneylog.screens.spendingEditor.makeSpendingEditorView
import romasku.moneylog.screens.spendingsList.makeSpendingsListView

fun viewSelector(inflater: LayoutInflater, parent: ViewGroup) = { screen: Screen ->
    when (screen) {
        is Screen.SpendingEditor -> makeSpendingEditorView(screen.store, inflater, parent)
        is Screen.SpendingsList -> makeSpendingsListView(screen.store, inflater, parent)
    }
}
