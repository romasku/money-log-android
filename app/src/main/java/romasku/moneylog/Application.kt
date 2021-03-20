package romasku.moneylog

import android.app.Application
import romasku.moneylog.lib.Navigator
import romasku.moneylog.services.storage.Storage
import romasku.moneylog.services.storage.makeStorageEffector
import romasku.moneylog.state.Route
import romasku.moneylog.state.Screen
import romasku.moneylog.state.makeNavigator

class MoneyLogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val storage = Storage()

        val effector = makeStorageEffector(storage)
        navigator = makeNavigator(effector)
    }

    companion object {
        lateinit var navigator: Navigator<Screen, Route>
    }
}
