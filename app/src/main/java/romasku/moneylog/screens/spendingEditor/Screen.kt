package romasku.moneylog.screens.spendingEditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.spending_editor.view.*
import romasku.moneylog.R
import romasku.moneylog.lib.linkDecimal
import romasku.moneylog.lib.linkString

fun makeSpendingEditorView(
    store: SpendingEditorStore,
    inflater: LayoutInflater,
    parentViewGroup: ViewGroup
): View {
    val view = inflater.inflate(R.layout.spending_editor, parentViewGroup, false)
    view.apply {
        store.apply {
            linkString(
                valueToEvent = { Event.NameEntered(it) },
                stateToValue = { it.name },
                editText = spending_name_input
            )

            store.subscribe { spending_name_layout.error = it.nameError }
            store.subscribe { spending_amount_layout.error = it.amountError }

            linkDecimal(
                valueToEvent = { Event.AmountEntered(it) },
                stateToValue = { it.amount },
                editText = spending_amount_input
            )
        }
        save_spending.setOnClickListener { store.dispatch(Event.SaveRequested) }
    }
    return view
}
