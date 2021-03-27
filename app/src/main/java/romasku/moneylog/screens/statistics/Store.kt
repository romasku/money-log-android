package romasku.moneylog.screens.statistics

import romasku.moneylog.GetDate
import romasku.moneylog.ListSpendings
import romasku.moneylog.lib.Dispatch
import romasku.moneylog.lib.Effector
import romasku.moneylog.lib.Store
import romasku.moneylog.lib.defDoCommand
import romasku.moneylog.lib.defInit
import romasku.moneylog.lib.defUpdate
import java.math.BigDecimal
import java.time.LocalDate

typealias StatisticsStore = Store<State, Event, Command>

data class StatisticsEntry(val category: String, val amount: BigDecimal)

data class DateRange(val start: LocalDate, val end: LocalDate)

data class State(val range: DateRange? = null,
                 val statistics: List<StatisticsEntry> = listOf())

sealed class Event {
    class DateRangeChanged(val range: DateRange) : Event()
    class GotStatistics(val statistics: List<StatisticsEntry>) : Event()
}

sealed class Command {
    object SetDefaultRange : Command()
    class CalculateStatistics(val range: DateRange) : Command()
}

val init = defInit { Pair(State(), Command.SetDefaultRange) }

val update = defUpdate { state: State, event: Event ->
    when (event) {
        is Event.DateRangeChanged -> Pair(state.copy(
            range = event.range,
        ), Command.CalculateStatistics(event.range)
        )
        is Event.GotStatistics -> Pair(state.copy(
            statistics = event.statistics
        ), null)
    }
}

val doCommand = defDoCommand<Command> { command ->
    when (command) {
        is Command.SetDefaultRange -> {
            val now = effect(GetDate)
            val weekAgo = now.minusDays(7)
            effect(Dispatch(Event.DateRangeChanged(DateRange(weekAgo, now))))
        }
        is Command.CalculateStatistics -> {
            val all = effect(ListSpendings)
            val filtered = all.filter { command.range.start <= it.data.date && it.data.date <= command.range.end }
            val sum = filtered.sumOf { it.data.amount }
            val statistics = listOf(StatisticsEntry("Total", sum))
            effect(Dispatch(Event.GotStatistics(statistics)))
        }
    }
}

fun makeStatisticsStore(effector: Effector): StatisticsStore = Store(
    init,
    update,
    doCommand, effector
)
