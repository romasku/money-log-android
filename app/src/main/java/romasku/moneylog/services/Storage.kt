package romasku.moneylog.services

import romasku.moneylog.ListSpendings
import romasku.moneylog.StoreSpending
import romasku.moneylog.entities.Spending
import romasku.moneylog.entities.SpendingData
import romasku.moneylog.lib.makeEffector
import romasku.moneylog.lib.plus

class Storage {
    private val spendings: MutableList<Spending> = mutableListOf()
    var id = 0

    private fun nextId(): String {
        id += 1
        return id.toString()
    }

    fun createSpending(data: SpendingData): Spending {
        val spending = Spending(nextId(), data)
        spendings.add(spending)
        return spending
    }

    fun listSpendings(): List<Spending> = spendings
}

fun makeStorageEffector(storage: Storage) =
    makeEffector { effect: StoreSpending -> storage.createSpending(effect.data) } +
        makeEffector { _: ListSpendings -> storage.listSpendings() }
