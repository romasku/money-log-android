package romasku.moneylog.screens.spendingsList

import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.Store
import romasku.moneylog.lib.defDoCommand
import romasku.moneylog.lib.defInit
import romasku.moneylog.lib.defUpdate

typealias SpendingsListStore = Store<State, Event, Command>

class State

sealed class Event

sealed class Command

val init = defInit { Pair(State(), null) }

val update = defUpdate { state: State, event: Event ->
    Pair(state, null)
}

val doCommand = defDoCommand<Command> {}

fun makeSpendingsListStore(effector: Effector): SpendingsListStore = Store(
    init,
    update,
    doCommand, effector
)
