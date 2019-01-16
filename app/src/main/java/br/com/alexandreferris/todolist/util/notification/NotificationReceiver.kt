package br.com.alexandreferris.todolist.util.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.util.constants.ActivityForResultEnum
import br.com.alexandreferris.todolist.util.constants.ItemConstans
import br.com.alexandreferris.todolist.view.EditItem
import org.apache.commons.lang3.math.NumberUtils

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        showNotification(context, intent)
    }

    private fun showNotification(context: Context?, receivedIntent: Intent?) {
        val itemId = receivedIntent?.getLongExtra("ITEM_ID", NumberUtils.LONG_ZERO)

        val contentIntent = Intent(context!!, EditItem::class.java)
        contentIntent.putExtra(ActivityForResultEnum.ITEM_ID, itemId)

        val pendingIntent = PendingIntent.getActivity(
                context,
                itemId!!.toInt(),
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(context, ItemConstans.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(receivedIntent?.getStringExtra("ITEM_TITLE"))
                .setContentText("${receivedIntent?.getStringExtra("ITEM_DESCRIPTION")}...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(itemId!!.toInt(), notificationBuilder.build())
        }
    }
}