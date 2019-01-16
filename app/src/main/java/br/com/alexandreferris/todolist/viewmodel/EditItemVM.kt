package br.com.alexandreferris.todolist.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.repository.remote.ItemHelper
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import br.com.alexandreferris.todolist.util.notification.NotificationReceiver
import org.apache.commons.lang3.math.NumberUtils
import java.util.*


open class EditItemVM(val context: Context): BaseVM() {

    private var itemHelper: ItemHelper = ItemHelper(context)
    private val item: MutableLiveData<Item> = MutableLiveData()

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Sends the Item parameter to the ItemHelper to insert in the database
     * @param Item
     * @return Boolean
     */
    fun saveItem(item: Item): Boolean {
        val resultId = itemHelper.saveItem(item)

        if (resultId > NumberUtils.LONG_ZERO) {
            item.id = resultId
            this.setNotification(item)

            return true
        }

        return false
    }

    fun updateItem(item: Item): Boolean {
        val result = itemHelper.updateItem(item)

        if (result)
            this.setNotification(item)

        return result
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

    private fun setNotification(item: Item) {
        if (item.alarmDateTime.toLong() > NumberUtils.LONG_ZERO) {
            // Create a calendar to get the correct time for the notification to popup.
            val calendarAlarmDateTime = Calendar.getInstance()
            calendarAlarmDateTime.timeInMillis = item.alarmDateTime.toLong()
            calendarAlarmDateTime.set(Calendar.SECOND, 0)

            // Intent to hold Item information
            val informationIntent = Intent(context, NotificationReceiver::class.java)
            informationIntent.putExtra("ITEM_ID", item.id)
            informationIntent.putExtra("ITEM_TITLE", item.title)
            informationIntent.putExtra("ITEM_DESCRIPTION", item.description)

            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                    context,
                    item.id.toInt(),
                    informationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            if (android.os.Build.VERSION.SDK_INT >= 19)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarmDateTime.timeInMillis, pendingIntent)
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarmDateTime.timeInMillis, pendingIntent)
        }
    }
}