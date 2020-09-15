package com.example.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils


class StudentProvider : ContentProvider() {

    val URL = "content://$PROVIDER_NAME/students"
    val CONTENT_URI = Uri.parse(URL)
    val ID = "id"
    val NAME = "name"
    val quantity = "quantity"
    val PRODUCTS = 1
    val PRODUCTS_ID = 2
    private val STUDENTS_PROJECTION_MAP: HashMap<String, String>? = null
    private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)
    var dbHelper: DataBaseHelper? = null
    var uriMatcher: UriMatcher? = null
    private val db: SQLiteDatabase? = null
    companion object {
        val PROVIDER_NAME = "com.example.contentprovider"
        val PRODUCTS_TABLE = "products"
        val CONTENT_URI: Uri = Uri.parse(
            "content://" + PROVIDER_NAME + "/" +
                    PRODUCTS_TABLE
        )
    }

    init {
        sURIMatcher.addURI(PROVIDER_NAME, PRODUCTS_TABLE, PRODUCTS)
        sURIMatcher.addURI(
            PROVIDER_NAME, PRODUCTS_TABLE + "/#",
            PRODUCTS_ID
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        /*val uriType = sURIMatcher.match(uri)

        val sqlDB = dbHelper!!.writableDatabase

        val id: Long
        when (uriType) {
            PRODUCTS -> id = sqlDB.insert(DataBaseHelper.TABLE_PRODUCTS, null, values)
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse(PRODUCTS_TABLE + "/" + id)*/
        val rowID: Long = db!!.insert(PRODUCTS_TABLE, "", values)

        /**
         * If record is added successfully
         */
        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }

        throw SQLException("Failed to add a record into $uri")

    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

       /* val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = DataBaseHelper.TABLE_PRODUCTS

        val uriType = sURIMatcher.match(uri)

        when (uriType) {
            PRODUCTS_ID -> queryBuilder.appendWhere(
                DataBaseHelper.KEY_ID + "="
                        + uri.lastPathSegment
            )
            PRODUCTS -> {
            }
            else -> throw IllegalArgumentException("Unknown URI")
        }

        val cursor = queryBuilder.query(
            dbHelper?.readableDatabase,
            projection, selection, selectionArgs, null, null,
            sortOrder
        )
        cursor.setNotificationUri(
            context!!.contentResolver,
            uri
        )
        return cursor*/
        val qb = SQLiteQueryBuilder()
        qb.tables = PRODUCTS_TABLE

        when (uriMatcher!!.match(uri)) {
            PRODUCTS -> qb.projectionMap = STUDENTS_PROJECTION_MAP
            PRODUCTS_ID -> qb.appendWhere(ID.toString() + "=" + uri.pathSegments[1])
            else -> {
            }
        }

        if (sortOrder == null || sortOrder === "") {
            //sortOrder = NAME
        }

        val c = qb.query(
            db, projection, selection,
            selectionArgs, null, null, sortOrder
        )
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    override fun onCreate(): Boolean {
        dbHelper = context?.let { DataBaseHelper(it) }
        return if (dbHelper == null) false else true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        var count = 0
        val id = uri.lastPathSegment
        when (uriMatcher!!.match(uri)) {
            PRODUCTS ->
            count = db!!.update(PRODUCTS_TABLE, values, selection, selectionArgs);

            PRODUCTS_ID ->
            if (TextUtils.isEmpty(selection)) {

                count = db!!.update(DataBaseHelper.TABLE_PRODUCTS,
                    values,
                    DataBaseHelper.KEY_ID + "=" + id, null)

            } else {
                count = db!!.update(DataBaseHelper.TABLE_PRODUCTS,
                    values,
                    DataBaseHelper.KEY_ID + "=" + id
                            + " and "
                            + selection,
                    selectionArgs)
            }

                throw SQLException("Failed to add a record into $uri")
        }

        getContext()!!.getContentResolver().notifyChange(uri, null);
        return count;
       /* val uriType = sURIMatcher.match(uri)
        val sqlDB: SQLiteDatabase = dbHelper!!.writableDatabase
        val rowsUpdated: Int

        when (uriType) {
            PRODUCTS -> rowsUpdated = sqlDB.update(DataBaseHelper.TABLE_PRODUCTS,
                values,
                selection,
                selectionArgs)
            PRODUCTS_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {

                    rowsUpdated = sqlDB.update(DataBaseHelper.TABLE_PRODUCTS,
                        values,
                        DataBaseHelper.KEY_ID + "=" + id, null)

                } else {
                    rowsUpdated = sqlDB.update(DataBaseHelper.TABLE_PRODUCTS,
                        values,
                        DataBaseHelper.KEY_ID + "=" + id
                                + " and "
                                + selection,
                        selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated*/

    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = dbHelper!!.writableDatabase

        val rowsDeleted: Int

        when (uriType) {
            PRODUCTS -> rowsDeleted = sqlDB.delete(DataBaseHelper.TABLE_PRODUCTS,
                selection,
                selectionArgs)

            PRODUCTS_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DataBaseHelper.TABLE_PRODUCTS,
                        DataBaseHelper.KEY_ID + "=" + id,
                        null)
                } else {
                    rowsDeleted = sqlDB.delete(DataBaseHelper.TABLE_PRODUCTS,
                        DataBaseHelper.KEY_ID + "=" + id
                                + " and " + selection,
                        selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(p0: Uri): String? {
        throw UnsupportedOperationException("Not yet implemented")
    }

}