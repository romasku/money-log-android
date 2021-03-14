package romasku.moneylog


import android.app.Application
import romasku.moneylog.services.storage.Storage
import romasku.moneylog.services.storage.makeStorageEffector
import romasku.moneylog.state.NavigatorStore
import romasku.moneylog.state.makeNavigator


class MoneyLogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val storage = Storage()

        val effector = makeStorageEffector(storage)
        navigatorStore = makeNavigator(effector)
    }

    companion object {
        lateinit var navigatorStore: NavigatorStore
    }
}