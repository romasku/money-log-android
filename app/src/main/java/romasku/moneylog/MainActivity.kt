package romasku.moneylog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import romasku.moneylog.lib.linkDecimal
import romasku.moneylog.lib.linkString
import romasku.moneylog.state.AmountEntered
import romasku.moneylog.state.NameEntered
import romasku.moneylog.state.SaveRequested
import romasku.moneylog.state.store

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store.linkString(
            valueToEvent = { NameEntered(it) },
            stateToValue = { it.name },
            editText = spending_name
        )

        store.linkDecimal(
            valueToEvent = { AmountEntered(it) },
            stateToValue = { it.amount },
            editText = spending_amount
        )

        save_spending.setOnClickListener { store.dispatch(SaveRequested) }
    }
}
