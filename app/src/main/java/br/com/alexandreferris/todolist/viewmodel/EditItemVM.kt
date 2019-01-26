package br.com.alexandreferris.todolist.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.repository.remote.ItemHelper
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject


open class EditItemVM @Inject constructor(private val itemHelper: ItemHelper): BaseVM() {
    private val item: MutableLiveData<Item> = MutableLiveData()

    /**
     * Sends the Item parameter to the ItemHelper to insert in the database
     * @param Item
     * @return Boolean
     */
    fun saveItem(item: Item): Long {
        val resultId = itemHelper.saveItem(item)

        if (resultId > NumberUtils.LONG_ZERO) {
            item.id = resultId
        }

        return resultId
    }

    fun updateItem(item: Item): Long {
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