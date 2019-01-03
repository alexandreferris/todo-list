package br.com.alexandreferris.todolist.repository.remote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import br.com.alexandreferris.todolist.model.Item


class ItemHelper(context: Context): DBHelper(context) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    /**
     * Insert the newly created item in the database
     * @param Item
     * @returns Boolean
     */
    fun saveItem(item: Item): Boolean {
        val values = ContentValues().apply {
            put(ItemColumns.COLUMN_TITLE, item.title)
            put(ItemColumns.COLUMN_DESCRIPTION, item.description)
            put(ItemColumns.COLUMN_CATEGORY, item.category)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = this.writableDatabase.insert(ItemColumns.TABLE_NAME, null, values)

        return (newRowId > 0)
    }

    /**
     * Return a ArrayList of Item from the database
     * @return ArrayList<Item>
     */
    fun getItems(): ArrayList<Item> {
        var items = ArrayList<Item>()

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
                ItemColumns.COLUMN_ID,
                ItemColumns.COLUMN_TITLE,
                ItemColumns.COLUMN_DESCRIPTION,
                ItemColumns.COLUMN_CATEGORY)

        // Filter results with WHERE
        val selectionArgs = null
        val selection = null

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${ItemColumns.COLUMN_ID} DESC"

        val cursor = this.readableDatabase.query(
                ItemColumns.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        )

        // val itemIds = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                val item = Item(
                        getLong(getColumnIndexOrThrow(ItemColumns.COLUMN_ID)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_TITLE)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_DESCRIPTION)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_CATEGORY)))
                items.add(item)
            }
        }

        return items
    }

    // Table contents are grouped together in an anonymous object.
    object ItemColumns : BaseColumns {
        const val TABLE_NAME = "item"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CATEGORY = "category"
    }

    companion object {
        private const val SQL_CREATE_ENTRIES =  "CREATE TABLE ${ItemColumns.TABLE_NAME} (" +
                "${ItemColumns.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${ItemColumns.COLUMN_TITLE} TEXT," +
                "${ItemColumns.COLUMN_DESCRIPTION} TEXT," +
                "${ItemColumns.COLUMN_CATEGORY} TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ItemColumns.TABLE_NAME}"
    }
}