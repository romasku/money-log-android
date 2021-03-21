package romasku.moneylog

import romasku.moneylog.entities.Spending
import romasku.moneylog.lib.Effect
import java.math.BigDecimal

sealed class StorageEffect<T> : Effect<T>()

data class StoreSpending(val name: String, val amount: BigDecimal) : StorageEffect<Spending>()
object ListSpendings : StorageEffect<List<Spending>>()
