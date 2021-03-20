package romasku.moneylog.state

import java.math.BigDecimal
import romasku.moneylog.lib.Effect
import romasku.moneylog.state.entities.Spending

sealed class StorageEffect<T> : Effect<T>()

data class StoreSpending(val name: String, val amount: BigDecimal) : StorageEffect<Spending>()
object ListSpendings : StorageEffect<List<Spending>>()
