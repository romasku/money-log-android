package romasku.moneylog.state.entities

import java.math.BigDecimal

data class PersistSpending(val name: String, val amount: BigDecimal)

data class Spending(val id: String, val name: String, val amount: BigDecimal)
