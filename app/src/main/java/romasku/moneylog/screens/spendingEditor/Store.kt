package romasku.moneylog.screens.spendingEditor

import romasku.moneylog.GetDate
import romasku.moneylog.entities.SpendingData
import romasku.moneylog.lib.Dispatch
import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.SetResult
import romasku.moneylog.lib.Store
import romasku.moneylog.lib.defDoCommand
import romasku.moneylog.lib.defInit
import romasku.moneylog.lib.defUpdate
import romasku.moneylog.screens.spendingEditor.Command.SaveSpending
import romasku.moneylog.screens.spendingEditor.Command.SetupDefaults
import java.math.BigDecimal
import java.time.LocalDate

typealias SpendingEditorStore = Store<State, Event, Command>

data class State(
    val name: String = "",
    val amount: BigDecimal? = null,
    val date: LocalDate? = null,
    val nameError: String? = null,
    val amountError: String? = null,
    val dateError: String? = null,
)

sealed class Event {

    data class NameEntered(val name: String) : Event()

    data class AmountEntered(val amount: BigDecimal?) : Event()

    data class DateEntered(val date: LocalDate?) : Event()

    object SaveRequested : Event()
}

sealed class Command {
    object SetupDefaults : Command()
    data class SaveSpending(val data: SpendingData) : Command()
}

val init = defInit { Pair(State(), SetupDefaults) }

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
        is Event.DateEntered -> Pair(
            state.copy(
                date = event.date,
                dateError = null
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
            val dateError = if (state.date == null) {
                "Date is required"
            } else {
                null
            }
            if (nameError == null && amountError == null && dateError == null) {
                Pair(
                    state,
                    SaveSpending(
                        SpendingData(
                            name = state.name,
                            amount = state.amount!!,
                            date = state.date!!,
                        )
                    )
                )
            } else {
                Pair(
                    state.copy(
                        nameError = nameError,
                        amountError = amountError,
                        dateError = dateError,
                    ),
                    null
                )
            }
        }
    }
}

val doCommand = defDoCommand { command: Command ->
    when (command) {
        is SaveSpending -> {
            effect(SetResult(command.data))
        }
        SetupDefaults -> {
            val date = effect(GetDate)
            effect(Dispatch(Event.DateEntered(date)))
        }
    }
}

fun makeSpendingEditorStore(effector: Effector): SpendingEditorStore =
    Store(init, update, doCommand, effector)
