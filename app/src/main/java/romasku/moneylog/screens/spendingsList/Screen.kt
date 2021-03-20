package romasku.moneylog.screens.spendingsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import romasku.moneylog.R

fun makeSpendingsListView(
    store: SpendingsListStore,
    inflater: LayoutInflater,
    parentViewGroup: ViewGroup
): View {
    return inflater.inflate(R.layout.spendings_list, parentViewGroup, false)
}
