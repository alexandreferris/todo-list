package br.com.alexandreferris.todolist.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.util.constants.ActivityForResultEnum
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.viewmodel.EditItemVM
import kotlinx.android.synthetic.main.activity_edit_item.*
import org.apache.commons.lang3.math.NumberUtils

class EditItem : AppCompatActivity(), View.OnClickListener {

    private lateinit var editItemVM: EditItemVM
    private var itemCreatedOrEdited: Boolean = false
    private var itemID: Long = 0
    private var item: Item = Item()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        initFields()
    }

    private fun initFields() {
        // ViewModel
        editItemVM = EditItemVM(this)

        // Button Click Listener
        btnSave.setOnClickListener(this)

        // Retrieving Extras (if having any)
        if (intent.hasExtra("ITEM_ID")) {
            itemID = intent.getLongExtra("ITEM_ID", 0)
            // Get Item with Observer
            editItemVM.getItem(itemID).observe(this, Observer { item ->
                this.item = item!!
                updateItemInformations()
            })
        }
    }

    private fun updateItemInformations() {
        if (item != null) {
            edtTitle.setText(item.title)
            edtDescription.setText(item.description)
            edtCategory.setText(item.category)
        }
    }

    private fun saveItem() {
        val item = Item(
                itemID,
                edtTitle.text.toString(),
                edtDescription.text.toString(),
                edtCategory.text.toString()
        )

        if (itemID == NumberUtils.LONG_ZERO)
            itemCreatedOrEdited = editItemVM.saveItem(item)
        else
            itemCreatedOrEdited = editItemVM.updateItem(item)

        if (itemCreatedOrEdited) {
            Snackbar.make(findViewById(R.id.rlEditItem), R.string.success_save_item, Snackbar.LENGTH_LONG).show()
        } else
            Snackbar.make(findViewById(R.id.rlEditItem), R.string.error_save_item, Snackbar.LENGTH_LONG).show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSave -> saveItem()
        }
    }

    override fun onBackPressed() {
        val parentScreen = Intent()
        parentScreen.putExtra("ITEM_CREATED_OR_EDITED", itemCreatedOrEdited)
        setResult(ActivityForResultEnum.ADD_OR_EDIT_ITEM, parentScreen)

        super.onBackPressed()
    }
}
