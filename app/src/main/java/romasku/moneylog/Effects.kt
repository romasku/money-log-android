package romasku.moneylog

import romasku.moneylog.entities.Spending
import romasku.moneylog.entities.SpendingData
import romasku.moneylog.lib.Effect

sealed class StorageEffect<T> : Effect<T>()

data class StoreSpending(val data: SpendingData) : StorageEffect<Spending>()
object ListSpendings : StorageEffect<List<Spending>>()
