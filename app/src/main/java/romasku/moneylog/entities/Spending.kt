package romasku.moneylog.entities

import java.math.BigDecimal

data class SpendingData(val name: String, val amount: BigDecimal)

data class Spending(val id: String, val data: SpendingData)
