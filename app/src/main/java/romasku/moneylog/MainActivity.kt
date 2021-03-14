package romasku.moneylog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import romasku.moneylog.lib.linkDecimal
import romasku.moneylog.lib.linkString
import romasku.moneylog.state.*
import romasku.moneylog.state.SpendingEditor.Event.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MoneyLogApplication.navigatorStore.subscribe { screen ->
            when(screen) {
                is SpendingEditorScreen -> screen.store.apply {
                    linkString(
                        valueToEvent = { NameEntered(it) },
                        stateToValue = { it.name },
                        editText = spending_name
                    )

                    linkDecimal(
                        valueToEvent = { AmountEntered(it) },
                        stateToValue = { it.amount },
                        editText = spending_amount
                    )

                    save_spending.setOnClickListener { dispatch(SaveRequested) }
                }
            }
        }


    }
}
