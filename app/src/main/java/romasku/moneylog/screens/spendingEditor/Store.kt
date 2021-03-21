package romasku.moneylog.screens.spendingEditor

import romasku.moneylog.entities.SpendingData
import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.SetResult
import romasku.moneylog.lib.Store
import romasku.moneylog.lib.defDoCommand
import romasku.moneylog.lib.defInit
import romasku.moneylog.lib.defUpdate
import java.math.BigDecimal

typealias SpendingEditorStore = Store<State, Event, Command>

data class State(
    val name: String = "",
    val amount: BigDecimal? = null,
    val nameError: String? = null,
    val amountError: String? = null
)

sealed class Event {

    data class NameEntered(val name: String) : Event()

    data class AmountEntered(val amount: BigDecimal?) : Event()

    object SaveRequested : Event()
}

sealed class Command {

    data class SaveSpending(val name: String, val amount: BigDecimal) : Command()
}

val init = defInit { Pair(State(), null) }

val update = defUpdate { state: State, event: Event ->
    when (event) {
        is Event.NameEntered -> Pair(
            state.copy(
                name = event.name,
                nameError = null
            ),
            null
        )
        is Event.AmountEntered -> Pair(
            state.copy(
                amount = event.amount,
                amountError = null
            ),
            null
        )
        is Event.SaveRequested -> {
            val nameError = if (state.name == "") {
                "Name is required"
            } else {
                null
            }
            val amountError = if (state.amount == null) {
                "Amount is required"
            } else {
                null
            }
            if (nameError == null && amountError == null) {
                Pair(state, Command.SaveSpending(state.name, state.amount!!))
            } else {
                Pair(
                    state.copy(
                        nameError = nameError,
                        amountError = amountError
                    ),
                    null
                )
            }
        }
    }
}

val doCommand = defDoCommand { command: Command ->
    when (command) {
        is Command.SaveSpending -> {
            effect(SetResult(SpendingData(command.name, command.amount)))
        }
    }
}

fun makeSpendingEditorStore(effector: Effector): SpendingEditorStore =
    Store(init, update, doCommand, effector)
