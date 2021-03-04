package romasku.moneylog.state

import java.math.BigDecimal
import romasku.moneylog.lib.Store

// Events

sealed class SpendingEditorEvent

data class NameEntered(val name: String) : SpendingEditorEvent()

data class AmountEntered(val amount: BigDecimal?) : SpendingEditorEvent()

object SaveRequested : SpendingEditorEvent()

// State

data class SpendingEditorState(val name: String, val amount: BigDecimal?)

val store =
    Store(SpendingEditorState("", null)) { state, event: SpendingEditorEvent ->
        when (event) {
            is NameEntered -> state.copy(
                name = event.name
            )
            is AmountEntered -> state.copy(
                amount = event.amount
            )
            is SaveRequested -> {
                println("Saving")
                state
            }
        }
    }
