package romasku.moneylog.entities

import java.math.BigDecimal
import java.time.LocalDate

data class SpendingData(val name: String, val amount: BigDecimal, val date: LocalDate)

data class Spending(val id: String, val data: SpendingData)
