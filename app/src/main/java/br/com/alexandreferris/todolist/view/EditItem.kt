package br.com.alexandreferris.todolist.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*
import android.widget.EditText
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.util.constants.ActivityForResultEnum
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.util.constants.ItemConstans
import br.com.alexandreferris.todolist.viewmodel.EditItemVM
import kotlinx.android.synthetic.main.activity_edit_item.*
import org.apache.commons.lang3.math.NumberUtils
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import br.com.alexandreferris.todolist.util.datetime.DateTimeUtil

class EditItem : AppCompatActivity(), View.OnClickListener {

    private lateinit var editItemVM: EditItemVM
    private var itemCreatedOrEdited: Boolean = false
    private var itemId: Long = 0
    private var item: Item = Item()

    // Toolbar MenuItem
    private lateinit var mIeEdit: MenuItem
    private lateinit var mIeCancel: MenuItem

    // Alarm
    private val calendarAlarmDateTime = Calendar.getInstance()

    private lateinit var listenerDatePicker: DatePickerDialog.OnDateSetListener
    private lateinit var listenerTimePicker: TimePickerDialog.OnTimeSetListener

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

        // Hide and Show alarm fields
        swAlarmNotification.setOnCheckedChangeListener { compoundButton, checked ->
            clAlarmDateTime.visibility = if (checked) View.VISIBLE else View.GONE
        }

        // Set current date and time to alarm inputs
        calendarAlarmDateTime.add(Calendar.HOUR_OF_DAY, NumberUtils.INTEGER_ONE)
        updateAlarmDateTime()

        // Listeners for Alarm date and time picker
        listenerDatePicker = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            calendarAlarmDateTime.set(Calendar.YEAR, year)
            calendarAlarmDateTime.set(Calendar.MONTH, month)
            calendarAlarmDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateAlarmDateTime()
        }

        listenerTimePicker = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendarAlarmDateTime.set(Calendar.HOUR_OF_DAY, hour)
            calendarAlarmDateTime.set(Calendar.MINUTE, minute)
            updateAlarmDateTime()
        }

        ivAlarmDate.setOnClickListener(this)
        ivAlarmTime.setOnClickListener(this)

        // Retrieving Extras (if having any)
        if (intent.hasExtra(ActivityForResultEnum.ITEM_ID)) {
            itemId = intent.getLongExtra(ActivityForResultEnum.ITEM_ID, 0)

            if (itemId > NumberUtils.LONG_ZERO) {
                // Get Item with Observer
                editItemVM.getItem(itemId).observe(this, Observer { item ->
                    this.item = item!!
                    updateItemInformations()
                })
            }
        }

        // Enable all fields if Item has no itemId
        if (itemId > NumberUtils.LONG_ZERO)
            disableFieldsForEditing()
        else
            enableFieldsForEditing()
    }

    private fun updateAlarmDateTime() {
        txtAlarmDate.text = DateTimeUtil.correctDayAndMonth(calendarAlarmDateTime.get(Calendar.DAY_OF_MONTH), calendarAlarmDateTime.get(Calendar.MONTH), calendarAlarmDateTime.get(Calendar.YEAR))
        txtAlarmTime.text = DateTimeUtil.addLeadingZeroToTime(calendarAlarmDateTime.get(Calendar.HOUR_OF_DAY), calendarAlarmDateTime.get(Calendar.MINUTE))
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

        // Priority Buttons
        when (item.priority) {
            ItemConstans.PRIORITY_NORMAL -> rbPriorityNormal.isChecked = true
            ItemConstans.PRIORITY_IMPORTANT -> rbPriorityImportant.isChecked = true
            ItemConstans.PRIORITY_CRITICAL -> rbPriorityCritical.isChecked = true
            else -> rbPriorityLow.isChecked = true
        }

        // Date and Time Alarm
        if (item.alarmDateTime.toLong() > NumberUtils.LONG_ZERO) {
            swAlarmNotification.isChecked = true

            val calendarDateTime = Calendar.getInstance()
            calendarDateTime.timeInMillis = item.alarmDateTime.toLong()

            txtAlarmDate.text = DateTimeUtil.correctDayAndMonth(calendarDateTime.get(Calendar.DAY_OF_MONTH), calendarDateTime.get(Calendar.MONTH), calendarDateTime.get(Calendar.YEAR))
            txtAlarmTime.text = DateTimeUtil.addLeadingZeroToTime(calendarDateTime.get(Calendar.HOUR_OF_DAY), calendarDateTime.get(Calendar.MINUTE))
        }
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

        // Enabling Priority Buttons
        rbPriorityLow.isEnabled = true
        rbPriorityNormal.isEnabled = true
        rbPriorityImportant.isEnabled = true
        rbPriorityCritical.isEnabled = true

        // Enabling Save Button
        btnSave.visibility = View.VISIBLE
        btnSave.isEnabled = true

        // Enabling Date and Time
        swAlarmNotification.isEnabled = true
        ivAlarmDate.isEnabled = true
        ivAlarmTime.isEnabled = true
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

        // Disabling Priority Buttons
        rbPriorityLow.isEnabled = false
        rbPriorityNormal.isEnabled = false
        rbPriorityImportant.isEnabled = false
        rbPriorityCritical.isEnabled = false

        // Disabling Save Button
        btnSave.visibility = View.GONE
        btnSave.isEnabled = false

        // Disabling Date and Time
        swAlarmNotification.isEnabled = false
        ivAlarmDate.isEnabled = false
        ivAlarmTime.isEnabled = false

        // Remove any changes made
        updateItemInformations()
    }

    private fun saveItem() {
        // Creates the Item to be saved
        val item = Item(
                itemId,
                edtTitle.text.toString(),
                edtDescription.text.toString(),
                edtCategory.text.toString(),
                item.completed,
                when (sgPriorityButton.checkedRadioButtonId) {
                    R.id.rbPriorityNormal -> ItemConstans.PRIORITY_NORMAL
                    R.id.rbPriorityImportant -> ItemConstans.PRIORITY_IMPORTANT
                    R.id.rbPriorityCritical -> ItemConstans.PRIORITY_CRITICAL
                    else -> ItemConstans.PRIORITY_LOW
                },
                if (swAlarmNotification.isChecked) calendarAlarmDateTime.timeInMillis.toString() else NumberUtils.INTEGER_ZERO.toString()
        )

        // Verify whether the Item needs to be created or updated
        if (itemId == NumberUtils.LONG_ZERO)
            itemCreatedOrEdited = editItemVM.saveItem(item)
        else
            itemCreatedOrEdited = editItemVM.updateItem(item)

        // Check if the Item has been saved successfully
        if (itemCreatedOrEdited) {
            // Check if the Item was updated, then make changes in the UI
            if (itemId > NumberUtils.LONG_ZERO) {
                editItemVM.loadItem(itemId)
                disableFieldsForEditing()
                mIeEdit.isVisible = true
                mIeCancel.isVisible = false
            }
            Snackbar.make(findViewById(R.id.rlEditItem), R.string.success_save_item, Snackbar.LENGTH_LONG).show()
        } else
            Snackbar.make(findViewById(R.id.rlEditItem), R.string.error_save_item, Snackbar.LENGTH_LONG).show()
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
                this,
                R.style.pickerDialogTheme,
                listenerDatePicker,
                calendarAlarmDateTime.get(Calendar.YEAR),
                calendarAlarmDateTime.get(Calendar.MONTH),
                calendarAlarmDateTime.get(Calendar.DAY_OF_MONTH))

        val listenerDatePickerConfirm = DialogInterface.OnClickListener { dialogInterface, which ->
            val finalDatePicker = datePicker.datePicker
            listenerDatePicker.onDateSet(finalDatePicker, finalDatePicker.year, finalDatePicker.month, finalDatePicker.dayOfMonth)
        }

        datePicker.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.confirm), listenerDatePickerConfirm)
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun showTimePicker() {
        val timePicker = TimePickerDialog(
                this,
                R.style.pickerDialogTheme,
                listenerTimePicker,
                calendarAlarmDateTime.get(Calendar.HOUR_OF_DAY),
                calendarAlarmDateTime.get(Calendar.MINUTE),
                false)

        timePicker.setTitle(getString(R.string.title_select_time))
        timePicker.show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSave -> saveItem()
            R.id.ivAlarmDate -> showDatePicker()
            R.id.ivAlarmTime -> showTimePicker()
        }
    }

    override fun onBackPressed() {
        val parentScreen = Intent()
        parentScreen.putExtra(ActivityForResultEnum.ITEM_CREATED_OR_EDITED, itemCreatedOrEdited)
        setResult(ActivityForResultEnum.ADD_OR_EDIT_ITEM, parentScreen)

        super.onBackPressed()
    }
}
