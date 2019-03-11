package br.com.alexandreferris.todolist.viewmodel

import android.arch.lifecycle.LiveData
import br.com.alexandreferris.todolist.model.Item
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.alexandreferris.todolist.repository.remote.ItemHelper
import br.com.alexandreferris.todolist.util.constants.ItemConstants
import javax.inject.Inject


open class MainVM @Inject constructor(private val itemHelper: ItemHelper): ViewModel() {
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
        val updateItemCompletedResult = itemHelper.updateItemCompleted(itemId, if (checked) ItemConstants.COMPLETED_YES else ItemConstants.COMPLETED_NO)

        if (updateItemCompletedResult)
            this.loadItems()

        return updateItemCompletedResult
    }

    override fun onCleared() {
        itemHelper.close()
        super.onCleared()
    }
}