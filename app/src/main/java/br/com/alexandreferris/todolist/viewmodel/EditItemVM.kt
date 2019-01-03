package br.com.alexandreferris.todolist.viewmodel

import android.content.Context
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.repository.remote.ItemHelper

open class EditItemVM(val context: Context): BaseVM() {

    private var itemHelper: ItemHelper = ItemHelper(context)

    /**
     * Sends the Item parameter to the ItemHelper to insert in the database
     * @param Item
     * @return Boolean
     */
    fun saveItem(item: Item): Boolean {
        return itemHelper.saveItem(item)
    }
}