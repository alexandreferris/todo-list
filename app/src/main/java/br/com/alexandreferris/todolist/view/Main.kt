package br.com.alexandreferris.todolist.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.alexandreferris.todolist.R
import br.com.alexandreferris.todolist.viewmodel.MainVM

class Main : AppCompatActivity() {

    private lateinit var mainVM: MainVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         mainVM = MainVM()

        mainVM.showToast(this)

    }
}
