package romasku.moneylog.lib

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import romasku.moneylog.lib.LinkParseResult.Fail
import romasku.moneylog.lib.LinkParseResult.OK
import java.math.BigDecimal

sealed class LinkParseResult<V> {
    data class OK<V>(val value: V) : LinkParseResult<V>()
    class Fail<V> : LinkParseResult<V>()
}

fun <S, E, C, V> Store<S, E, C>.link(
    stringToValue: (String) -> LinkParseResult<V>,
    valueToString: (V) -> String,
    valueToEvent: (V) -> E,
    stateToValue: (S) -> V,
    editText: EditText
) {
    editText.doOnTextChanged { text, _, _, _ ->
        val string = text.toString()
        when (val parseResult = stringToValue(string)) {
            is LinkParseResult.OK<V> -> {
                val event = valueToEvent(parseResult.value)
                dispatch(event)
            }
            is LinkParseResult.Fail -> {
                editText.setText(valueToString(stateToValue(this.current)))
            }
        }
    }
    subscribe {
        val stateValue = stateToValue(it)
        val shouldUpdate = when (val viewValue = stringToValue(editText.text.toString())) {
            is Fail -> true
            is OK -> viewValue.value != stateValue
        }
        if (shouldUpdate) {
            editText.setText(valueToString(stateValue))
        }
    }
}

fun <S, E, M> Store<S, E, M>.linkString(
    valueToEvent: (String) -> E,
    stateToValue: (S) -> String,
    editText: EditText
) {
    link({ LinkParseResult.OK(it) }, { it }, valueToEvent, stateToValue, editText)
}

fun <S, E, M> Store<S, E, M>.linkDecimal(
    valueToEvent: (BigDecimal?) -> E,
    stateToValue: (S) -> BigDecimal?,
    editText: EditText
) {
    link(
        {
            val decimal = it.toBigDecimalOrNull()
            when {
                it == "" -> LinkParseResult.OK(null)
                decimal != null -> LinkParseResult.OK(decimal)
                else -> LinkParseResult.Fail()
            }
        },
        {
            it?.toPlainString() ?: ""
        },
        valueToEvent, stateToValue, editText
    )
}
