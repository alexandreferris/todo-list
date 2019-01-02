package br.com.alexandreferris.todolist.viewmodel

import android.content.Context
import android.widget.Toast

open class MainVM: BaseVM() {
    fun showToast(context: Context) {
        Toast.makeText(context, "Testing Toast and MVVM", Toast.LENGTH_LONG).show()
    }
}