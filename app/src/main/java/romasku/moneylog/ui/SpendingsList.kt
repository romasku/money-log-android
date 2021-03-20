package romasku.moneylog.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.spending_editor.*
import kotlinx.android.synthetic.main.spending_editor.view.*
import romasku.moneylog.R
import romasku.moneylog.state.SpendingsListStore

fun makeSpendingsListView(
    store: SpendingsListStore,
    inflater: LayoutInflater,
    parentViewGroup: ViewGroup
): View {
    return inflater.inflate(R.layout.spendings_list, parentViewGroup, false)
}
