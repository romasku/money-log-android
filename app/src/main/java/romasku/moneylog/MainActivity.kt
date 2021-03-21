package romasku.moneylog

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import romasku.moneylog.lib.ViewNavigator
import romasku.moneylog.lib.makeUINavigator
import romasku.moneylog.screens.Screen
import romasku.moneylog.screens.viewSelector

class MainActivity : AppCompatActivity() {
    lateinit var viewNavigator: ViewNavigator<Screen, View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frame = FrameLayout(this)
        setContentView(frame)

        viewNavigator =
            MoneyLogApplication.navigator.makeUINavigator(viewSelector(layoutInflater, frame))
        viewNavigator.subscribe {
            frame.removeAllViews()
            frame.addView(it)
        }
    }

    override fun onBackPressed() {
        // TODO: pass navigation through ViewStore
        if (MoneyLogApplication.navigator.current.size == 1) {
            super.onBackPressed()
        } else {
            MoneyLogApplication.navigator.dispatchBack()
        }
    }
}
