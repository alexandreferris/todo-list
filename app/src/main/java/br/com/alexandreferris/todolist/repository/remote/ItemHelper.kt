package br.com.alexandreferris.todolist.repository.remote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import br.com.alexandreferris.todolist.model.Item
import br.com.alexandreferris.todolist.util.constants.ItemConstans
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class ItemHelper @Inject constructor(context: Context): DBHelper(context) {

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
    fun saveItem(item: Item): Long {
        val values = ContentValues().apply {
            put(ItemColumns.COLUMN_TITLE, item.title)
            put(ItemColumns.COLUMN_DESCRIPTION, item.description)
            put(ItemColumns.COLUMN_CATEGORY, item.category)
            put(ItemColumns.COLUMN_PRIORITY, item.priority)
            put(ItemColumns.COLUMN_ALARM_DATE_TIME, item.alarmDateTime)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = this.writableDatabase.insert(ItemColumns.TABLE_NAME, null, values)

        return newRowId
    }

    /**
     * Updates the item in the database
     * @param Item
     * @return Boolean
     */
    fun updateItem(item: Item): Long {
        // New values
        val values = ContentValues().apply {
            put(ItemColumns.COLUMN_TITLE, item.title)
            put(ItemColumns.COLUMN_DESCRIPTION, item.description)
            put(ItemColumns.COLUMN_CATEGORY, item.category)
            put(ItemColumns.COLUMN_PRIORITY, item.priority)
            put(ItemColumns.COLUMN_ALARM_DATE_TIME, item.alarmDateTime)
        }

        // Which row to update, based on the ID
        val selection = "${ItemColumns.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(item.id.toString())
        val count = this.writableDatabase.update(
                ItemColumns.TABLE_NAME,
                values,
                selection,
                selectionArgs)

        return if (count > NumberUtils.LONG_ZERO) item.id else NumberUtils.LONG_ZERO
    }

    fun updateItemCompleted(itemId: Long, completed: String): Boolean {
        // New values
        val values = ContentValues().apply {
            put(ItemColumns.COLUMN_COMPLETED, completed)
        }

        // Which row to update, based on the ID
        val selection = "${ItemColumns.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(itemId.toString())
        val count = this.writableDatabase.update(
                ItemColumns.TABLE_NAME,
                values,
                selection,
                selectionArgs)

        return (count > NumberUtils.INTEGER_ZERO)
    }

    fun getItem(itemId: Long): Item {
        val where = ItemColumns.COLUMN_ID + " = ?"
        val whereArgs = arrayOf(itemId.toString())

        val sortOrder = ItemColumns.COLUMN_ID + " ASC"

        val cursor = this.readableDatabase.query(
                ItemColumns.TABLE_NAME, // The table to query
                allColumnsProjection, // The array of columns to return (pass null to get all)
                where, // The columns for the WHERE clause
                whereArgs, // don't group the rows
                null, null, // don't filter by row groups
                sortOrder, // The sort order
                NumberUtils.INTEGER_ONE.toString()
        )


        var item = Item()
        while (cursor.moveToNext()) {
            item = Item(
                    cursor.getLong(cursor.getColumnIndexOrThrow(ItemColumns.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ItemColumns.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ItemColumns.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ItemColumns.COLUMN_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ItemColumns.COLUMN_COMPLETED)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ItemColumns.COLUMN_PRIORITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ItemColumns.COLUMN_ALARM_DATE_TIME)))
        }
        cursor.close()

        return item
    }

    /**
     * Removes an item from the database and returns true if one or more items has been removed
     * @param itemId: Long
     * @return Boolean
     */
    fun removeItem(itemId: Long): Boolean {
        // Define 'where' part of query.
        val selection = "${ItemColumns.COLUMN_ID} = ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(itemId.toString())
        // Issue SQL statement.
        val deletedRows = this.writableDatabase.delete(ItemColumns.TABLE_NAME, selection, selectionArgs)

        return (deletedRows > NumberUtils.INTEGER_ZERO)
    }

    /**
     * Return a ArrayList of Item from the database
     * @return ArrayList<Item>
     */
    fun getItems(): ArrayList<Item> {
        var items = ArrayList<Item>()

        // Filter results with WHERE
        val selectionArgs = null
        val selection = null

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${ItemColumns.COLUMN_ID} DESC"

        val cursor = this.readableDatabase.query(
                ItemColumns.TABLE_NAME,   // The table to query
                allColumnsProjection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val item = Item(
                        getLong(getColumnIndexOrThrow(ItemColumns.COLUMN_ID)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_TITLE)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_DESCRIPTION)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_CATEGORY)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_COMPLETED)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_PRIORITY)),
                        getString(getColumnIndexOrThrow(ItemColumns.COLUMN_ALARM_DATE_TIME)))
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
        const val COLUMN_COMPLETED = "completed"
        const val COLUMN_PRIORITY = "priority"
        const val COLUMN_ALARM_DATE_TIME = "alarm_date_time"
    }

    companion object {
        private const val SQL_CREATE_ENTRIES =  "CREATE TABLE ${ItemColumns.TABLE_NAME} (" +
                "${ItemColumns.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${ItemColumns.COLUMN_TITLE} TEXT," +
                "${ItemColumns.COLUMN_DESCRIPTION} TEXT," +
                "${ItemColumns.COLUMN_CATEGORY} TEXT," +
                "${ItemColumns.COLUMN_COMPLETED} TEXT CHECK( ${ItemColumns.COLUMN_COMPLETED } IN ('${ItemConstans.COMPLETED_YES}','${ItemConstans.COMPLETED_NO}') ) NOT NULL DEFAULT '${ItemConstans.COMPLETED_NO}'," +
                "${ItemColumns.COLUMN_PRIORITY} TEXT CHECK( ${ItemColumns.COLUMN_PRIORITY } IN ('${ItemConstans.PRIORITY_LOW}','${ItemConstans.PRIORITY_NORMAL}','${ItemConstans.PRIORITY_IMPORTANT}','${ItemConstans.PRIORITY_CRITICAL}') ) NOT NULL DEFAULT '${ItemConstans.PRIORITY_LOW}'," +
                "${ItemColumns.COLUMN_ALARM_DATE_TIME} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ItemColumns.TABLE_NAME}"


        private val allColumnsProjection = arrayOf(
                ItemColumns.COLUMN_ID,
                ItemColumns.COLUMN_TITLE,
                ItemColumns.COLUMN_DESCRIPTION,
                ItemColumns.COLUMN_CATEGORY,
                ItemColumns.COLUMN_COMPLETED,
                ItemColumns.COLUMN_PRIORITY,
                ItemColumns.COLUMN_ALARM_DATE_TIME)
    }
}