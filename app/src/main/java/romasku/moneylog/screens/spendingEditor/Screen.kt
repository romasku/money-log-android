package romasku.moneylog.screens.spendingEditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.spending_editor.view.*
import romasku.moneylog.R
import romasku.moneylog.lib.linkDecimal
import romasku.moneylog.lib.linkString
import romasku.moneylog.screens.dateToStr
import romasku.moneylog.screens.millisecondUTCToLocalDate
import romasku.moneylog.screens.toUTCEpochMillisecond


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
            store.subscribe { spending_date_layout.error = it.dateError }

            linkDecimal(
                valueToEvent = { Event.AmountEntered(it) },
                stateToValue = { it.amount },
                editText = spending_amount_input
            )

            store.subscribe { state -> state.date?.also { spending_date_input.setText(dateToStr(it)) } }

            spending_date_input.setOnClickListener {
                val builder = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                store.current.date?.also {
                    builder.setSelection(it.toUTCEpochMillisecond())
                }
                val picker = builder.build()
                picker.addOnPositiveButtonClickListener {
                    store.dispatch(Event.DateEntered(millisecondUTCToLocalDate(it)))
                }
                picker.show((view.context as AppCompatActivity).supportFragmentManager, null)
            }
        }
        save_spending.setOnClickListener { store.dispatch(Event.SaveRequested) }
    }
    return view
}
