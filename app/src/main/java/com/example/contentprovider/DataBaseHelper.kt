package com.example.contentprovider

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // Database Name
    private val myCR: ContentResolver
    var COLUMN_PRODUCTNAME: String = "name"
    var COLUMN_QUANTITY: String = "quantity"
    var COLOUM_ID = "id"

    init {
        myCR = context.contentResolver
    }

    companion object {
        var DATABASE_NAME = "student_database"
        val DATABASE_VERSION = 1
        val TABLE_PRODUCTS = "products"
        val KEY_ID = "id"
        val KEY_NAME = "name"
        val KEY_QUANTITY = "quantity"
        var TAG = "tag"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE =
            ("CREATE TABLE " + TABLE_PRODUCTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_QUANTITY + " TEXT" + ")")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS" + TABLE_PRODUCTS)
        onCreate(db)
    }

    fun addProduct(product: Product) {

        val values = ContentValues()
        values.put(KEY_NAME, product.productName)
        values.put(KEY_QUANTITY, product.quantity)

        myCR.insert(StudentProvider.CONTENT_URI, values)
    }

    fun findProduct(productname: String): Product? {
        val projection = arrayOf(KEY_ID, KEY_NAME, KEY_QUANTITY)

        val selection = "productname = \"" + productname + "\""

        val cursor = myCR.query(
            StudentProvider.CONTENT_URI,
            projection, selection, null, null
        )

        var product: Product? = null

        if (cursor!!.moveToFirst()) {
            cursor.moveToFirst()
            val id = Integer.parseInt(cursor!!.getString(0))
            val productName = cursor.getString(1)
            val quantity = Integer.parseInt(cursor.getString(2))

            product = Product(id, productname, quantity)
            cursor.close()
        }
        return product
    }

    fun deleteProduct(productname: String): Boolean {

        var result = false

        val selection = "productname = \"" + productname + "\""

        val rowsDeleted = myCR.delete(
            StudentProvider.CONTENT_URI,
            selection, null
        )

        if (rowsDeleted > 0)
            result = true

        return result
    }


}