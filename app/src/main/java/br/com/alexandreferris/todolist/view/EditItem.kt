package br.com.alexandreferris.todolist.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.helper.constants.ActivityForResultEnum
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.viewmodel.EditItemVM
import kotlinx.android.synthetic.main.activity_edit_item.*

class EditItem : AppCompatActivity(), View.OnClickListener {

    private lateinit var editItemVM: EditItemVM
    private var itemCreatedOrEdited: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        editItemVM = EditItemVM(this)

        initFields()
    }

    private fun initFields() {
        btnSave.setOnClickListener(this)
    }

    private fun saveItem() {
        val item = Item(
                0,
                edtTitle.text.toString(),
                edtDescription.text.toString(),
                edtCategory.text.toString()
        )

        if (editItemVM.saveItem(item)) {
            itemCreatedOrEdited = true
            Snackbar.make(findViewById(R.id.clEditItem), R.string.success_save_item, Snackbar.LENGTH_LONG).show()
        } else
            Snackbar.make(findViewById(R.id.clEditItem), R.string.error_save_item, Snackbar.LENGTH_LONG).show()
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
