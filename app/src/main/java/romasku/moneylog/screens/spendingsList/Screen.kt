package romasku.moneylog.screens.spendingsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.spendings_list.view.*
import kotlinx.android.synthetic.main.spendings_list_item.view.*
import romasku.moneylog.R
import romasku.moneylog.entities.Spending

fun makeSpendingsListView(
    store: SpendingsListStore,
    inflater: LayoutInflater,
    parentViewGroup: ViewGroup
): View {
    val view = inflater.inflate(R.layout.spendings_list, parentViewGroup, false)
    view.apply {
        store.subscribe {
            spendings_list.adapter = SpendingsAdapter(it.spendings)
        }
        spendings_list.setHasFixedSize(true)
        add_new_spending_fab.setOnClickListener { store.dispatch(Event.AddNewSpendingRequested) }
    }
    return view
}

class SpendingsAdapter(private val spendings: List<Spending>) : RecyclerView.Adapter<SpendingsAdapter.SpendingViewHolder>() {
    class SpendingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(spending: Spending) {
            itemView.apply {
                spending_name.text = spending.data.name
                spending_amount.text = spending.data.amount.toPlainString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.spendings_list_item, parent, false)

        return SpendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        holder.bind(spendings[position])
    }

    override fun getItemCount() = spendings.size
}
