package romasku.moneylog.screens.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.spendings_list.view.*
import kotlinx.android.synthetic.main.spendings_list_item.view.*
import kotlinx.android.synthetic.main.statistics.view.*
import kotlinx.android.synthetic.main.statistics_entry.view.*
import romasku.moneylog.R
import romasku.moneylog.screens.dateToStr
import romasku.moneylog.screens.millisecondUTCToLocalDate
import romasku.moneylog.screens.toUTCEpochMillisecond

fun makeStatisticsView(
    store: StatisticsStore,
    inflater: LayoutInflater,
    parentViewGroup: ViewGroup
): View {
    val view = inflater.inflate(R.layout.statistics, parentViewGroup, false)
    view.apply {
        store.subscribe {
            statistics_entries.adapter = StatisticsAdapter(it.statistics)
            if (it.range != null ) {
                statistics_range.text = "${dateToStr(it.range.start)} - ${dateToStr(it.range.end)}"
            } else {
                statistics_range.text = ""
            }
        }
        statistics_entries.setHasFixedSize(true)
        statistics_range.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select range for statistics")
            store.current.range?.also {
                builder.setSelection(Pair(
                    it.start.toUTCEpochMillisecond(),
                    it.end.toUTCEpochMillisecond(),
                ))
            }
            val picker = builder.build()
            picker.addOnPositiveButtonClickListener {
                val start = it.first
                val end = it.second
                if (start != null && end != null) {
                    store.dispatch(Event.DateRangeChanged(DateRange(
                        millisecondUTCToLocalDate(start),
                        millisecondUTCToLocalDate(end),
                    )))
                }
            }
            picker.show((view.context as AppCompatActivity).supportFragmentManager, null)
        }
    }
    return view
}

class StatisticsAdapter(private val statistics: List<StatisticsEntry>) : RecyclerView.Adapter<StatisticsAdapter.StatViewHolder>() {
    class StatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(statEntry: StatisticsEntry) {
            itemView.apply {
                category_name.text = statEntry.category
                category_total_amount.text = statEntry.amount.toPlainString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.statistics_entry, parent, false)

        return StatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(statistics[position])
    }

    override fun getItemCount(): Int {
        return statistics.size
    }
}
