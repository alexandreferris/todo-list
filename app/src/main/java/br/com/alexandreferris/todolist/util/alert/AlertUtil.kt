package br.com.alexandreferris.todolist.util.alert

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import br.com.alexandreferris.todolist.R

open class AlertUtil {

    private val diButtonNeutralOK = DialogInterface.OnClickListener { dialogInterface, _ ->
        dialogInterface.dismiss()
    }

    fun alertMessage(context: Context, title: String, message: String) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)


        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok), diButtonNeutralOK)

        alertDialog.show()
    }
}