package br.com.alexandreferris.todolist.view

import android.app.*
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
import br.com.alexandreferris.todolist.util.constants.ItemConstants
import br.com.alexandreferris.todolist.viewmodel.EditItemVM
import kotlinx.android.synthetic.main.activity_edit_item.*
import org.apache.commons.lang3.math.NumberUtils
import java.util.*
import android.content.Context
import android.content.DialogInterface
import br.com.alexandreferris.todolist.di.DaggerAppComponent
import br.com.alexandreferris.todolist.di.AppModule
import br.com.alexandreferris.todolist.util.datetime.DateTimeUtil
import br.com.alexandreferris.todolist.util.notification.NotificationReceiver
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import org.apache.commons.lang3.StringUtils
import javax.inject.Inject

class EditItem : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var editItemVM: EditItemVM
    private var itemCreatedOrEdited: Long = NumberUtils.LONG_ZERO
    private var item: Item = Item()

    // Toolbar MenuItem
    private lateinit var mIeEdit: MenuItem
    private lateinit var mIeCancel: MenuItem

    // Alarm
    private val calendarAlarmDateTime = Calendar.getInstance()

    private lateinit var listenerDatePicker: DatePickerDialog.OnDateSetListener
    private lateinit var listenerTimePicker: TimePickerDialog.OnTimeSetListener

    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        Fabric.with(this, Crashlytics())

        initFields()
    }

    private fun initFields() {
        // ViewModel
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
                .injectEditItem(this)

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Button Click Listener
        btnSave.setOnClickListener(this)

        // Hide and Show alarm fields
        swAlarmNotification.setOnCheckedChangeListener { _, checked ->
            clAlarmDateTime.visibility = if (checked) View.VISIBLE else View.GONE
        }

        // Set current date and time to alarm inputs
        calendarAlarmDateTime.add(Calendar.HOUR_OF_DAY, NumberUtils.INTEGER_ONE)
        updateAlarmDateTime()

        // Listeners for Alarm date and time picker
        listenerDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendarAlarmDateTime.set(Calendar.YEAR, year)
            calendarAlarmDateTime.set(Calendar.MONTH, month)
            calendarAlarmDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateAlarmDateTime()
        }

        listenerTimePicker = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendarAlarmDateTime.set(Calendar.HOUR_OF_DAY, hour)
            calendarAlarmDateTime.set(Calendar.MINUTE, minute)
            updateAlarmDateTime()
        }

        ivAlarmDate.setOnClickListener(this)
        ivAlarmTime.setOnClickListener(this)

        // Retrieving Extras (if having any)
        if (intent.hasExtra(ActivityForResultEnum.ITEM_ID)) {
            this.item.id = intent.getLongExtra(ActivityForResultEnum.ITEM_ID, 0)

            if (this.item.id > NumberUtils.LONG_ZERO) {
                // Get Item with Observer
                editItemVM.getItem(this.item.id).observe(this, Observer { item ->
                    this.item = item!!
                    updateItemInformations()
                })
            }
        }

        // Enable all fields if Item has no itemId
        if (this.item.id > NumberUtils.LONG_ZERO)
            enableDisableFieldsForEditing(false)
        else
            enableDisableFieldsForEditing(true)
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
                enableDisableFieldsForEditing(true)
                return true
            }
            R.id.ieCancel -> {
                mIeEdit.isVisible = true
                mIeCancel.isVisible = false
                enableDisableFieldsForEditing(false)
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
            ItemConstants.PRIORITY_NORMAL -> rbPriorityNormal.isChecked = true
            ItemConstants.PRIORITY_IMPORTANT -> rbPriorityImportant.isChecked = true
            ItemConstants.PRIORITY_CRITICAL -> rbPriorityCritical.isChecked = true
            else -> rbPriorityLow.isChecked = true
        }

        // Date and Time Alarm
        if (item.alarmDateTime.toLong() > NumberUtils.LONG_ZERO) {
            swAlarmNotification.isChecked = true

            calendarAlarmDateTime.timeInMillis = item.alarmDateTime.toLong()

            txtAlarmDate.text = DateTimeUtil.correctDayAndMonth(calendarAlarmDateTime.get(Calendar.DAY_OF_MONTH), calendarAlarmDateTime.get(Calendar.MONTH), calendarAlarmDateTime.get(Calendar.YEAR))
            txtAlarmTime.text = DateTimeUtil.addLeadingZeroToTime(calendarAlarmDateTime.get(Calendar.HOUR_OF_DAY), calendarAlarmDateTime.get(Calendar.MINUTE))
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

    private fun enableDisableFieldsForEditing(enable: Boolean) {
        // Enabling EditText Fields
        edtTitle.isEnabled = enable
        edtCategory.isEnabled = enable
        edtDescription.isEnabled = enable

        // Changing Text Appearance
        edtTitle.textSize = if (enable) "14.0".toFloat() else "18.0".toFloat()
        edtTitle.typeface = if (enable) Typeface.DEFAULT else Typeface.DEFAULT_BOLD

        txtItemSpaceLine.setBackgroundColor(if (enable) resources.getColor(R.color.gray_slight_dark) else resources.getColor(R.color.colorAccent))

        // Setting default background to EditText Fields
        edtTitle.background = if (enable) resources.getDrawable(R.drawable.border_full_green) else null
        edtCategory.background = if (enable) resources.getDrawable(R.drawable.border_full_gray) else null
        edtDescription.background = if (enable) resources.getDrawable(R.drawable.border_full_gray) else null

        // Add onFocus to change background of EditText Fields
        if (enable) {
            edtTitle.setOnFocusChangeListener { view, focused ->
                updateEditTextBackground(edtTitle, view, focused)
            }
            edtCategory.setOnFocusChangeListener { view, focused ->
                updateEditTextBackground(edtCategory, view, focused)
            }
            edtDescription.setOnFocusChangeListener { view, focused ->
                updateEditTextBackground(edtDescription, view, focused)
            }
        } else {
            edtTitle.onFocusChangeListener = null
            edtCategory.onFocusChangeListener = null
            edtDescription.onFocusChangeListener = null
        }

        // Enabling Priority Buttons
        rbPriorityLow.isEnabled = enable
        rbPriorityNormal.isEnabled = enable
        rbPriorityImportant.isEnabled = enable
        rbPriorityCritical.isEnabled = enable

        // Enabling Save Button
        btnSave.visibility = if (enable) View.VISIBLE else View.GONE
        btnSave.isEnabled = enable

        // Enabling Date and Time
        swAlarmNotification.isEnabled = enable
        ivAlarmDate.isEnabled = enable
        ivAlarmTime.isEnabled = enable

        if (!enable)
            updateItemInformations()
    }

    private fun saveItem() {
        try {
            validateFields()

            // Creates the Item to be saved
            val item = Item(
                    this.item.id,
                    edtTitle.text.toString(),
                    edtDescription.text.toString(),
                    edtCategory.text.toString(),
                    item.completed,
                    when (sgPriorityButton.checkedRadioButtonId) {
                        R.id.rbPriorityNormal -> ItemConstants.PRIORITY_NORMAL
                        R.id.rbPriorityImportant -> ItemConstants.PRIORITY_IMPORTANT
                        R.id.rbPriorityCritical -> ItemConstants.PRIORITY_CRITICAL
                        else -> ItemConstants.PRIORITY_LOW
                    },
                    if (swAlarmNotification.isChecked) calendarAlarmDateTime.timeInMillis.toString() else NumberUtils.INTEGER_ZERO.toString()
            )

            // Verify whether the Item needs to be created or updated
            if (this.item.id == NumberUtils.LONG_ZERO)
                itemCreatedOrEdited = editItemVM.saveItem(item)
            else
                itemCreatedOrEdited = editItemVM.updateItem(item)

            // Check if the Item has been saved successfully
            if (itemCreatedOrEdited > NumberUtils.LONG_ZERO) {
                // Check if the Item was updated, then make changes in the UI
                if (this.item.id > NumberUtils.LONG_ZERO) {
                    editItemVM.loadItem(this.item.id)
                    enableDisableFieldsForEditing(false)
                    mIeEdit.isVisible = true
                    mIeCancel.isVisible = false
                }

                item.id = itemCreatedOrEdited

                // Set the notification alarm
                setNotification(item)

                Snackbar.make(findViewById(R.id.rlEditItem), R.string.success_save_item, Snackbar.LENGTH_LONG).show()
            } else
                Snackbar.make(findViewById(R.id.rlEditItem), R.string.error_save_item, Snackbar.LENGTH_LONG).show()
        } catch (inputMismatchException: InputMismatchException) {
            Snackbar.make(findViewById(R.id.rlEditItem), inputMismatchException.message!!, Snackbar.LENGTH_LONG).show()
        }
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

    private fun validateFields() {
        if (StringUtils.isEmpty(edtTitle.text))
            throw InputMismatchException(getString(R.string.field_error_title))
        if (StringUtils.isEmpty(edtCategory.text))
            throw InputMismatchException(getString(R.string.field_error_category))
    }

    private fun setNotification(item: Item) {
        if (item.alarmDateTime.toLong() > NumberUtils.LONG_ZERO) {
            // Create a calendar to get the correct time for the notification to popup.
            val calendarAlarmDateTime = Calendar.getInstance()
            calendarAlarmDateTime.timeInMillis = item.alarmDateTime.toLong()
            calendarAlarmDateTime.set(Calendar.SECOND, 0)

            // Intent to hold Item information
            val informationIntent = Intent(this, NotificationReceiver::class.java)
            informationIntent.putExtra("ITEM_ID", item.id)
            informationIntent.putExtra("ITEM_TITLE", item.title)
            informationIntent.putExtra("ITEM_DESCRIPTION", item.description)
            informationIntent.putExtra("ITEM_PRIORITY", item.priority)

            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                    this,
                    item.id.toInt(),
                    informationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            if (android.os.Build.VERSION.SDK_INT >= 19)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarmDateTime.timeInMillis, pendingIntent)
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarmDateTime.timeInMillis, pendingIntent)
        }
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
        parentScreen.putExtra(ActivityForResultEnum.ITEM_CREATED_OR_EDITED, (itemCreatedOrEdited > NumberUtils.LONG_ZERO))
        setResult(ActivityForResultEnum.ADD_OR_EDIT_ITEM, parentScreen)

        super.onBackPressed()
    }
}
