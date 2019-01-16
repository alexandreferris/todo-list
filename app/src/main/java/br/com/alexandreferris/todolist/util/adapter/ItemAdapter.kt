package br.com.alexandreferris.todolist.util.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.util.constants.ItemConstans
import br.com.alexandreferris.todolist.util.datetime.DateTimeUtil
import kotlinx.android.synthetic.main.list_item.view.*
import org.apache.commons.lang3.math.NumberUtils
import java.util.*

class ItemAdapter(val itemClickListener: (Long, Boolean) -> Unit, val checkboxCompletedListener: (Long, Boolean) -> Unit): RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    private var itemList: ArrayList<Item> = ArrayList<Item>()
    var itemListCompleted: String = ItemConstans.COMPLETED_NO

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    // class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    class MyViewHolder(val view: View): RecyclerView.ViewHolder(view)

    // Listener adapter for setOnClickListener and setOnLongClickListener
    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, delete: Boolean) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, false)
        }
        itemView.setOnLongClickListener {
            event.invoke(adapterPosition, true)
            return@setOnLongClickListener true
        }
        return this
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false) as View

        return MyViewHolder(view).listen { pos, delete ->
            itemClickListener.invoke(itemList[pos].id, delete)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Item background based on Priority Level
        var cardBackgroundColor: Int
        var cardStatusBackgroundColor: Int
        when (itemList[position].priority) {
            ItemConstans.PRIORITY_NORMAL -> {
                cardBackgroundColor = R.drawable.border_full_priority_normal
                cardStatusBackgroundColor = R.drawable.border_left_priority_normal
            }
            ItemConstans.PRIORITY_IMPORTANT -> {
                cardBackgroundColor = R.drawable.border_full_priority_important
                cardStatusBackgroundColor = R.drawable.border_left_priority_important
            }
            ItemConstans.PRIORITY_CRITICAL -> {
                cardBackgroundColor = R.drawable.border_full_priority_critical
                cardStatusBackgroundColor = R.drawable.border_left_priority_critical
            }
            else -> {
                cardBackgroundColor = R.drawable.border_full_priority_low
                cardStatusBackgroundColor = R.drawable.border_left_priority_low
            }
        }

        holder.view.clItemCard.background = holder.view.resources.getDrawable(cardBackgroundColor)
        holder.view.txtItemStatusBG.background = holder.view.resources.getDrawable(cardStatusBackgroundColor)

        // Name and Category
        holder.view.txtItemName.text = itemList[position].title
        holder.view.txtItemCategory.text = itemList[position].category

        // Completed Status
        holder.view.chkCompleted.isChecked = (itemList[position].completed.compareTo(ItemConstans.COMPLETED_YES) == NumberUtils.INTEGER_ZERO)
        holder.view.chkCompleted.setOnCheckedChangeListener { _, checked ->
            checkboxCompletedListener.invoke(itemList[position].id, checked)
            if ((itemListCompleted.compareTo(ItemConstans.COMPLETED_YES) == NumberUtils.INTEGER_ZERO && !checked)
                    ||(itemListCompleted.compareTo(ItemConstans.COMPLETED_NO) == NumberUtils.INTEGER_ZERO && checked) )
                    notifyDataSetChanged()
        }

        // Date and Time
        hideShowDateTimeFields(holder, false)
        if (itemList[position].alarmDateTime.toLong() > NumberUtils.LONG_ZERO) {
            hideShowDateTimeFields(holder, true)

            val calendarDateTime = Calendar.getInstance()
            calendarDateTime.timeInMillis = itemList[position].alarmDateTime.toLong()

            holder.view.txtDate.text = DateTimeUtil.correctDayAndMonth(calendarDateTime.get(Calendar.DAY_OF_MONTH), calendarDateTime.get(Calendar.MONTH), calendarDateTime.get(Calendar.YEAR))
            holder.view.txtTime.text = DateTimeUtil.addLeadingZeroToTime(calendarDateTime.get(Calendar.HOUR_OF_DAY), calendarDateTime.get(Calendar.MINUTE))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemList.size

    override fun getItemId(position: Int): Long {
        return itemList[position].id
    }

    fun setItemList(itemList: ArrayList<Item>) {
        this.itemList = ArrayList(itemList.filter { item -> item.completed == this.itemListCompleted })
    }

    private fun hideShowDateTimeFields(holder: MyViewHolder, show: Boolean) {
        holder.view.ivDate.visibility = if (show) View.VISIBLE else View.GONE
        holder.view.ivTime.visibility = if (show) View.VISIBLE else View.GONE
        holder.view.txtDate.visibility = if (show) View.VISIBLE else View.GONE
        holder.view.txtTime.visibility = if (show) View.VISIBLE else View.GONE
    }

}