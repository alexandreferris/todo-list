package br.com.alexandreferris.todolist.util.alert

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.widget.TextView
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.view.Main

open class ItemRemoveDialog: AppCompatDialogFragment, DialogInterface.OnClickListener {

    var item: Item = Item()

    constructor()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(activity)

        alertDialog.setTitle("")

        val layout = activity?.layoutInflater?.inflate(R.layout.dialog_item_remove, null)
        layout?.findViewById<TextView>(R.id.txtItemName)?.text = context?.getString(R.string.item_remove)?.replace("item_name", item.title)
        alertDialog.setView(layout)

        alertDialog.setPositiveButton(activity?.getString(R.string.yes), this)
        alertDialog.setNegativeButton(activity?.getString(R.string.no), this)

        return alertDialog.create()
    }



    override fun onClick(dialogInterface: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE ->
                (activity as Main).removeItem(item.id)
        }
    }
}