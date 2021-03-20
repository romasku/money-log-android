package romasku.moneylog.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.spending_editor.view.*
import romasku.moneylog.R
import romasku.moneylog.lib.linkDecimal
import romasku.moneylog.lib.linkString
import romasku.moneylog.state.SpendingEditor
import romasku.moneylog.state.SpendingEditorStore

fun makeSpendingEditorView(
    store: SpendingEditorStore,
    inflater: LayoutInflater,
    parentViewGroup: ViewGroup
): View {
    val view = inflater.inflate(R.layout.spending_editor, parentViewGroup, false)
    view.apply {
        store.apply {
            linkString(
                valueToEvent = { SpendingEditor.Event.NameEntered(it) },
                stateToValue = { it.name },
                editText = spending_name
            )

            linkDecimal(
                valueToEvent = { SpendingEditor.Event.AmountEntered(it) },
                stateToValue = { it.amount },
                editText = spending_amount
            )
        }
        save_spending.setOnClickListener { store.dispatch(SpendingEditor.Event.SaveRequested) }
    }
    return view
}
