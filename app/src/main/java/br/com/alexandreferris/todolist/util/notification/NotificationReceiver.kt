package br.com.alexandreferris.todolist.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.util.constants.ActivityForResultEnum
import br.com.alexandreferris.todolist.util.constants.ItemConstants
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            val importanceLevel = NotificationManager.IMPORTANCE_LOW
            when (receivedIntent.getStringExtra("ITEM_PRIORITY")) {
                ItemConstants.PRIORITY_NORMAL, ItemConstants.PRIORITY_IMPORTANT -> NotificationManager.IMPORTANCE_DEFAULT
                ItemConstants.PRIORITY_CRITICAL -> NotificationManager.IMPORTANCE_HIGH
            }

            val notificationChannel = NotificationChannel(ItemConstants.NOTIFICATION_CHANNEL_ID, ItemConstants.NOTIFICATION_CHANNEL_ID, importanceLevel)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)

            val manager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, ItemConstants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(receivedIntent.getStringExtra("ITEM_TITLE"))
                .setContentText("${receivedIntent.getStringExtra("ITEM_DESCRIPTION")}...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            this.
            notify(itemId.toInt(), notificationBuilder.build())
        }
    }
}