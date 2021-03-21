package romasku.moneylog.screens.spendingsList

import romasku.moneylog.ListSpendings
import romasku.moneylog.StoreSpending
import romasku.moneylog.entities.Spending
import romasku.moneylog.entities.SpendingData
import romasku.moneylog.lib.Dispatch
import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.StartForResult
import romasku.moneylog.lib.Store
import romasku.moneylog.lib.defDoCommand
import romasku.moneylog.lib.defInit
import romasku.moneylog.lib.defUpdate
import romasku.moneylog.screens.Route
import romasku.moneylog.screens.spendingsList.Command.AddNew
import romasku.moneylog.screens.spendingsList.Command.LoadSpendings
import romasku.moneylog.screens.spendingsList.Event.AddNewSpendingRequested
import romasku.moneylog.screens.spendingsList.Event.SpendingsLoaded

typealias SpendingsListStore = Store<State, Event, Command>

data class State(val spendings: List<Spending> = listOf())

sealed class Event {
    object AddNewSpendingRequested : Event()
    class SpendingsLoaded(val list: List<Spending>) : Event()
}

sealed class Command {
    object LoadSpendings : Command()
    object AddNew : Command()
}

val init = defInit { Pair(State(), LoadSpendings) }

val update = defUpdate { state: State, event: Event ->
    when (event) {
        AddNewSpendingRequested -> Pair(state, AddNew)
        is SpendingsLoaded -> Pair(
            state.copy(
                spendings = event.list
            ),
            null
        )
    }
}

val doCommand = defDoCommand<Command> { command ->
    val doLoadSpendings = suspend {
        val spendings = effect(ListSpendings)
        effect(Dispatch(SpendingsLoaded(spendings)))
    }

    when (command) {
        LoadSpendings -> doLoadSpendings()
        AddNew -> {
            val result = effect(StartForResult(Route.NewSpending)) as SpendingData
            effect(StoreSpending(result))
            doLoadSpendings()
        }
    }
}

fun makeSpendingsListStore(effector: Effector): SpendingsListStore = Store(
    init,
    update,
    doCommand, effector
)
