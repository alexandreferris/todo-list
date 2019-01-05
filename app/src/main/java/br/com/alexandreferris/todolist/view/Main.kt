package br.com.alexandreferris.todolist.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.util.adapter.ItemAdapter
import br.com.alexandreferris.todolist.util.alert.ItemRemoveDialog
import br.com.alexandreferris.todolist.util.constants.ActivityForResultEnum
import br.com.alexandreferris.todolist.util.constants.ItemConstans
import br.com.alexandreferris.todolist.viewmodel.MainVM
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.lang3.math.NumberUtils


class Main : AppCompatActivity(), View.OnClickListener {

    private lateinit var mainVM: MainVM

    // RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ItemAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    // Toolbar MenuItem
    private lateinit var mIeCompleted: MenuItem
    private lateinit var mIeUncompleted: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFields()
    }

    private fun initFields() {
        // ViewModel
        mainVM = MainVM(this)

        // Button Click Listener
        fabAddNewItem.setOnClickListener(this)

        // Handling Item Listing
        viewManager = LinearLayoutManager(this)
        viewAdapter = ItemAdapter ({ itemId, delete ->
            if (delete)
                showDeleteItemAlert(itemId)
            else
                showAddNewItemScreen(itemId)
        }, { itemId, checked ->
            updateItemCompleted(itemId, checked)
        })

        recyclerView = findViewById<RecyclerView>(R.id.rvItems).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        // Get Items List with Observer
        mainVM.getItems().observe(this, Observer { itemList ->
            viewAdapter.setItemList(itemList!!)

            rvItems.visibility = View.GONE
            txtNoUncompleted.visibility = View.GONE
            txtNoCompleted.visibility = View.GONE

            if (viewAdapter.itemCount > NumberUtils.INTEGER_ZERO)
                rvItems.visibility = View.VISIBLE
            else {
                if (viewAdapter.itemListCompleted == ItemConstans.COMPLETED_NO)
                    txtNoUncompleted.visibility = View.VISIBLE
                else
                    txtNoCompleted.visibility = View.VISIBLE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_list, menu)

        mIeCompleted = menu!!.findItem(R.id.ieCompleted)
        mIeUncompleted = menu.findItem(R.id.ieUncompleted)

        mIeUncompleted.isVisible = false

        return true
    }

    private fun updateItemListing(showCompleted: Boolean) {
        mIeCompleted.isVisible = !showCompleted
        mIeUncompleted.isVisible = showCompleted
        viewAdapter.itemListCompleted = if (showCompleted) ItemConstans.COMPLETED_YES else ItemConstans.COMPLETED_NO
        txtCompleted.visibility = if (showCompleted) View.VISIBLE else View.GONE
        mainVM.loadItems()
        viewAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.ieCompleted -> {
                updateItemListing(true)
                return true
            }
            R.id.ieUncompleted -> {
                updateItemListing(false)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateItemCompleted(itemId: Long, checked: Boolean) {
        if (mainVM.updateItemCompleted(itemId, checked)) {
            Snackbar.make(findViewById(R.id.clMain), R.string.success_complete_item, Snackbar.LENGTH_LONG).show()
        } else
            Snackbar.make(findViewById(R.id.clMain), R.string.error_complete_item, Snackbar.LENGTH_LONG).show()

    }

    private fun showAddNewItemScreen(itemID: Long) {
        val addNewItemScreen = Intent(this, EditItem::class.java)
        addNewItemScreen.putExtra(ActivityForResultEnum.ITEM_ID, itemID)

        startActivityForResult(addNewItemScreen, ActivityForResultEnum.ADD_OR_EDIT_ITEM)
    }

    private fun showDeleteItemAlert(itemId: Long) {
        val item = mainVM.getItem(itemId)
        val itemRemoveDialog = ItemRemoveDialog()
        itemRemoveDialog.item = item
        itemRemoveDialog.show(this@Main.supportFragmentManager, ActivityForResultEnum.ITEM_REMOVE_DIALOG)
    }

    fun removeItem(itemId: Long) {
        if (mainVM.removeItem(itemId)) {
            Snackbar.make(findViewById(R.id.clMain), R.string.success_remove_item, Snackbar.LENGTH_LONG).show()
            viewAdapter.notifyDataSetChanged()
        } else
            Snackbar.make(findViewById(R.id.clMain), R.string.error_remove_item, Snackbar.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            ActivityForResultEnum.ADD_OR_EDIT_ITEM -> {
                val itemCreatedOrEdited: Boolean? = data?.getBooleanExtra(ActivityForResultEnum.ITEM_CREATED_OR_EDITED, false)

                if (itemCreatedOrEdited!!) {
                    mainVM.loadItems()
                    viewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.fabAddNewItem -> showAddNewItemScreen(0)
        }
    }
}
