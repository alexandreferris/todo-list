package br.com.alexandreferris.todolist.viewmodel

import android.arch.lifecycle.LiveData
import android.content.Context
import br.com.alexandreferris.todolist.model.Item
import android.arch.lifecycle.MutableLiveData
import br.com.alexandreferris.todolist.repository.remote.ItemHelper
import br.com.alexandreferris.todolist.util.constants.ItemConstans


open class MainVM(context: Context): BaseVM() {

    private var itemHelper: ItemHelper = ItemHelper(context)
    private val itemList: MutableLiveData<ArrayList<Item>> = MutableLiveData()

    fun loadItems() {
        this.itemList.value = itemHelper.getItems()
    }

    fun getItems(): LiveData<ArrayList<Item>> {
        loadItems()

        return itemList
    }

    fun getItem(itemId: Long): Item {
        return itemHelper.getItem(itemId)
    }

    fun removeItem(itemId: Long): Boolean {
        val removeItemResult = itemHelper.removeItem(itemId)

        if (removeItemResult)
            this.loadItems()

        return removeItemResult
    }

    fun updateItemCompleted(itemId: Long, checked: Boolean): Boolean {
        val updateItemCompletedResult = itemHelper.updateItemCompleted(itemId, if (checked) ItemConstans.COMPLETED_YES else ItemConstans.COMPLETED_NO)

        if (updateItemCompletedResult)
            this.loadItems()

        return updateItemCompletedResult
    }
}