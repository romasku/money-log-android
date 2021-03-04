package romasku.moneylog.lib

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import java.math.BigDecimal

fun <S, E, V> Store<S, E>.link(
    stringToValue: (String) -> V,
    valueToString: (V) -> String,
    valueToEvent: (V) -> E,
    stateToValue: (S) -> V,
    editText: EditText
) {
    editText.doOnTextChanged { text, _, _, _ ->
        val string = text.toString()
        val value = stringToValue(string)
        val event = valueToEvent(value)
        dispatch(event)
    }
    subscribe {
        val stateValue = stateToValue(it)
        val viewValue = stringToValue(editText.text.toString())
        if (stateValue != viewValue) {
            editText.setText(valueToString(stateValue))
        }
    }
}

fun <S, E> Store<S, E>.linkString(
    valueToEvent: (String) -> E,
    stateToValue: (S) -> String,
    editText: EditText
) {
    link({ it }, { it }, valueToEvent, stateToValue, editText)
}

fun <S, E> Store<S, E>.linkDecimal(
    valueToEvent: (BigDecimal?) -> E,
    stateToValue: (S) -> BigDecimal?,
    editText: EditText
) {
    link(
        { it.toBigDecimalOrNull() },
        {
            it?.toPlainString() ?: ""
        },
        valueToEvent, stateToValue, editText
    )
}
