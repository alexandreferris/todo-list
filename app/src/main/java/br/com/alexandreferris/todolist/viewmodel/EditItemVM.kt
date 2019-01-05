package br.com.alexandreferris.todolist.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.repository.remote.ItemHelper

open class EditItemVM(val context: Context): BaseVM() {

    private var itemHelper: ItemHelper = ItemHelper(context)
    private val item: MutableLiveData<Item> = MutableLiveData()

    /**
     * Sends the Item parameter to the ItemHelper to insert in the database
     * @param Item
     * @return Boolean
     */
    fun saveItem(item: Item): Boolean {
        return itemHelper.saveItem(item)
    }

    fun updateItem(item: Item): Boolean {
        return itemHelper.updateItem(item)
    }

    fun loadItem(itemId: Long) {
        this.item.value = itemHelper.getItem(itemId)
    }

    fun getItem(itemId: Long): LiveData<Item> {
        loadItem(itemId)
        return this.item
    }

    override fun onCleared() {
        itemHelper.close()
        super.onCleared()
    }
}