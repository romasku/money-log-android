package romasku.moneylog

import java.math.BigDecimal
import romasku.moneylog.entities.Spending
import romasku.moneylog.lib.Effect

sealed class StorageEffect<T> : Effect<T>()

data class StoreSpending(val name: String, val amount: BigDecimal) : StorageEffect<Spending>()
object ListSpendings : StorageEffect<List<Spending>>()
