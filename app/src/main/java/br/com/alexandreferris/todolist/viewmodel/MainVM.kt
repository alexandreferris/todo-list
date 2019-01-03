package br.com.alexandreferris.todolist.viewmodel

import android.arch.lifecycle.LiveData
import android.content.Context
import br.com.alexandreferris.todolist.model.Item
import android.arch.lifecycle.MutableLiveData
import br.com.alexandreferris.todolist.repository.remote.ItemHelper


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
}