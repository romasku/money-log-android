package romasku.moneylog

import romasku.moneylog.entities.Spending
import romasku.moneylog.entities.SpendingData
import romasku.moneylog.lib.Effect
import java.time.LocalDate

// Storage effects

data class StoreSpending(val data: SpendingData) : Effect<Spending>()
object ListSpendings : Effect<List<Spending>>()

// Date effects

object GetDate : Effect<LocalDate>()
