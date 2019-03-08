package br.com.alexandreferris.todolist.repository.remote

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Creation of the table
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }


    companion object {
        const val DATABASE = "Organizr.db"
        const val DATABASE_VERSION = 1
    }
}