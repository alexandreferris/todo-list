package br.com.alexandreferris.todolist.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.helper.adapter.ItemAdapter
import br.com.alexandreferris.todolist.helper.constants.ActivityForResultEnum
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.viewmodel.MainVM
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.keySet



class Main : AppCompatActivity(), View.OnClickListener {

    private lateinit var mainVM: MainVM

    // RecyclerView
    private lateinit var recyclerView: RecyclerView
    // private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewAdapter: ItemAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFields()
    }

    var itemListObserver: Observer<ArrayList<Item>> = Observer { t ->
        viewAdapter.setItemList(t!!)
    }

    /*
    var itemListObserver: Observer<ArrayList<Item>> = Observer<ArrayList<Item>>() {
        fun onChanged(@Nullable items: ArrayList<Item>) {
            // Update UI
            viewAdapter.setItemList(items)
        }
    }
    */

    private fun initFields() {
        // ViewModel
        mainVM = MainVM(this)

        // Button Click
        fabAddNewItem.setOnClickListener(this)

        // Handling Item Listing
        viewManager = LinearLayoutManager(this)
        viewAdapter = ItemAdapter()

        recyclerView = findViewById<RecyclerView>(R.id.rvItems).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        // mainVM.getItems().observe(this, itemListObserver)
        mainVM.getItems().observe(this, itemListObserver)
    }

    private fun showAddNewItemScreen(position: Int?) {
        val addNewItemScreen = Intent(this, EditItem::class.java)
        addNewItemScreen.putExtra(
                "ITEM_ID",
                if (position != null)
                    viewAdapter.getItemId(position)
                else
                    null
        )

        startActivityForResult(addNewItemScreen, ActivityForResultEnum.ADD_OR_EDIT_ITEM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            ActivityForResultEnum.ADD_OR_EDIT_ITEM -> {
                val itemCreatedOrEdited: Boolean? = data?.getBooleanExtra("ITEM_CREATED_OR_EDITED", false)

                if (itemCreatedOrEdited!!)
                    mainVM.loadItems()
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.fabAddNewItem -> showAddNewItemScreen(null)
        }
    }
}
