package br.com.alexandreferris.todolist.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.transition.Visibility
import android.view.*
import android.widget.EditText
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

    // Toolbar MenuItem
    private lateinit var mIeEdit: MenuItem
    private lateinit var mIeCancel: MenuItem

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

            if (itemID > NumberUtils.LONG_ZERO) {
                // Get Item with Observer
                editItemVM.getItem(itemID).observe(this, Observer { item ->
                    this.item = item!!
                    updateItemInformations()
                })

                // Load editing toolbar
                // this.startActionMode(mActionModeCallback)
            }
        }

        if (itemID == NumberUtils.LONG_ZERO)
            enableFieldsForEditing()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (item.id > NumberUtils.LONG_ZERO) {
            menuInflater.inflate(R.menu.item_edit, menu)

            mIeEdit = menu!!.findItem(R.id.ieEdit)
            mIeCancel = menu.findItem(R.id.ieCancel)

            mIeCancel.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.ieEdit -> {
                mIeEdit.isVisible = false
                mIeCancel.isVisible = true
                enableFieldsForEditing()
                return true
            }
            R.id.ieCancel -> {
                mIeEdit.isVisible = true
                mIeCancel.isVisible = false
                disableFieldsForEditing()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateItemInformations() {
        edtTitle.setText(item.title)
        edtDescription.setText(item.description)
        edtCategory.setText(item.category)
    }

    private fun updateEditTextBackground(editText: EditText, view: View, focused: Boolean) {
        if (focused) {
            view.background = resources.getDrawable(R.drawable.border_full_green)
            editText.setHintTextColor(resources.getColor(R.color.colorAccent))
        } else {
            view.background = resources.getDrawable(R.drawable.border_full_gray)
            editText.setHintTextColor(resources.getColor(R.color.gray_slight_dark))
        }
    }

    private fun enableFieldsForEditing() {
        // Enabling EditText Fields
        edtTitle.isEnabled = true
        edtCategory.isEnabled = true
        edtDescription.isEnabled = true

        // Changing Text Appearance
        edtTitle.textSize = "14.0".toFloat()
        edtTitle.typeface = Typeface.DEFAULT

        txtItemSpaceLine.setBackgroundColor(resources.getColor(R.color.gray_slight_dark))

        // Setting default background to EditText Fields
        edtTitle.background = resources.getDrawable(R.drawable.border_full_green)
        edtCategory.background = resources.getDrawable(R.drawable.border_full_gray)
        edtDescription.background = resources.getDrawable(R.drawable.border_full_gray)

        // Add onFocus to change background of EditText Fields
        edtTitle.setOnFocusChangeListener { view, focused ->
            updateEditTextBackground(edtTitle, view, focused)
        }
        edtCategory.setOnFocusChangeListener { view, focused ->
            updateEditTextBackground(edtCategory, view, focused)
        }
        edtDescription.setOnFocusChangeListener { view, focused ->
            updateEditTextBackground(edtDescription, view, focused)
        }


        // Enabling Save Button
        btnSave.visibility = View.VISIBLE
        btnSave.isEnabled = true
    }

    private fun disableFieldsForEditing() {
        // Disabling EditText Fields
        edtTitle.isEnabled = false
        edtCategory.isEnabled = false
        edtDescription.isEnabled = false

        // Changing Text Appearance to default
        edtTitle.textSize = "18.0".toFloat()
        edtTitle.typeface = Typeface.DEFAULT_BOLD

        txtItemSpaceLine.setBackgroundColor(resources.getColor(R.color.colorAccent))

        // Setting default background to EditText Fields
        edtTitle.background = null
        edtCategory.background = null
        edtDescription.background = null

        // Remove onFocus to change background of EditText Fields
        edtTitle.onFocusChangeListener = null
        edtCategory.onFocusChangeListener = null
        edtDescription.onFocusChangeListener = null

        // Disabling Save Button
        btnSave.visibility = View.GONE
        btnSave.isEnabled = false

        // Remove any changes made
        updateItemInformations()
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
