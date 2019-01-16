package br.com.alexandreferris.todolist.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.util.constants.ItemConstans
import org.apache.commons.lang3.math.NumberUtils

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        showNotification(context, intent)
    }

    private fun showNotification(context: Context?, intent: Intent?) {
        val notificationBuilder = NotificationCompat.Builder(context!!, ItemConstans.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(intent?.getStringExtra("ITEM_TITLE"))
                .setContentText("${intent?.getStringExtra("ITEM_DESCRIPTION")}...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(intent?.getLongExtra("ITEM_ID", NumberUtils.LONG_ZERO)!!.toInt(), notificationBuilder.build())
        }
    }
}