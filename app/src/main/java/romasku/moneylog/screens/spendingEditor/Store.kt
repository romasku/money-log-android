package romasku.moneylog.screens.spendingEditor

import romasku.moneylog.StoreSpending
import java.math.BigDecimal
import romasku.moneylog.lib.*
import romasku.moneylog.screens.Route

typealias SpendingEditorStore = Store<SpendingEditor.State, SpendingEditor.Event, SpendingEditor.Command>

object SpendingEditor : StoreDefs<SpendingEditor.State, SpendingEditor.Event, SpendingEditor.Command> {

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

    override val init = defInit { Pair(State(), null) }

    override val update = defUpdate {
        state: State, event: Event ->
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
                } else { null }
                val amountError = if (state.amount == null) {
                    "Amount is required"
                } else { null }
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

    override val doCommand = defDoCommand {
        command: Command ->
        when (command) {
            is Command.SaveSpending -> {
                effect(StoreSpending(command.name, command.amount))
                effect(NavigateTo(Route.SpendingList))
            }
        }
    }
}
