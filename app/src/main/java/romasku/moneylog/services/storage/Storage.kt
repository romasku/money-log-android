package romasku.moneylog.services.storage

import android.util.Log
import romasku.moneylog.lib.makeEffector
import romasku.moneylog.lib.plus
import java.math.BigDecimal
import romasku.moneylog.state.ListSpendings
import romasku.moneylog.state.StoreSpending
import romasku.moneylog.state.entities.Spending

class Storage {
    val spendings: MutableList<Spending> = mutableListOf()
    var id = 0

    private fun nextId(): String {
        id += 1
        return id.toString()
    }

    fun createSpending(name: String, amount: BigDecimal): Spending {
        val spending = Spending(nextId(), name, amount)
        spendings.add(spending)
        return spending
    }

    fun listSpendings(): List<Spending> = spendings
}


fun makeStorageEffector(storage: Storage) =
    makeEffector { effect: StoreSpending -> storage.createSpending(effect.name, effect.amount) } +
    makeEffector { _: ListSpendings -> storage.listSpendings()  }


