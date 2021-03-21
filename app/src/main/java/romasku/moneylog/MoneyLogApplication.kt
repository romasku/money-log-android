package romasku.moneylog

import android.app.Application
import romasku.moneylog.lib.Navigator
import romasku.moneylog.lib.plus
import romasku.moneylog.screens.Route
import romasku.moneylog.screens.Screen
import romasku.moneylog.screens.makeNavigator
import romasku.moneylog.services.Storage
import romasku.moneylog.services.makeStorageEffector
import romasku.moneylog.services.makeTimeEffector

class MoneyLogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val storage = Storage()

        val effector = makeStorageEffector(storage) +
            makeTimeEffector()
        navigator = makeNavigator(effector)
    }

    companion object {
        lateinit var navigator: Navigator<Screen, Route>
    }
}
