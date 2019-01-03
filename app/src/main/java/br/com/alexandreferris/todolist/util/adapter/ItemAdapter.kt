package br.com.alexandreferris.todolist.util.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.model.Item
import kotlinx.android.synthetic.main.list_item.view.*

class ItemAdapter(val listener: (Long, Boolean) -> Unit): RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {
    private var itemList: ArrayList<Item> = ArrayList<Item>()

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
            listener.invoke(itemList[pos].id, delete)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Later get Status and apply the backgrounds
        holder.view.clItemCard.background = holder.view.resources.getDrawable(R.drawable.border_full_green)
        holder.view.txtItemStatusBG.background = holder.view.resources.getDrawable(R.drawable.border_left_green)

        holder.view.txtItemName.text = itemList[position].title
        holder.view.txtItemCategory.text = itemList[position].category
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemList.size

    override fun getItemId(position: Int): Long {
        return itemList[position].id
    }

    fun setItemList(itemList: ArrayList<Item>) {
        this.itemList = itemList
    }
}