package romasku.moneylog.state

import romasku.moneylog.lib.*

typealias SpendingsListStore = Store<SpendingsList.State, SpendingsList.Event, SpendingsList.Command>

object SpendingsList : StoreDefs<SpendingsList.State, SpendingsList.Event, SpendingsList.Command> {

    class State

    sealed class Event

    sealed class Command

    override val init = defInit { Pair(State(), null) }

    override val update = defUpdate {
        state: State, event: Event ->
        Pair(state, null)
    }

    override val doCommand = defDoCommand<Command> {}
}
