package com.example.bricklist.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.bricklist.database.model.Project
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DataHelper(private val context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "BrickList.db"
        private const val DB_VERSION = 1
    }

    fun openDatabase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath(DB_NAME)

        if (!dbFile.exists()) {
            try {
                val checkDB = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)

                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }

        }
        return writableDatabase;
    }

    private fun copyDatabase(dbFile: File) {
        val inputStream = context.assets.open(DB_NAME)
        val os = FileOutputStream(dbFile)

        val buffer = ByteArray(1024)
        while (inputStream.read(buffer) > 0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        inputStream.close()
        Log.d("#DB", "completed..")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val dbFile = context.getDatabasePath(DB_NAME)

        if (!dbFile.exists()) {
            try {
                val checkDB = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)

                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dbFile = context.getDatabasePath(DB_NAME)
        if (dbFile.exists()) {
            try {
                dbFile.delete();
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }
        }

        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun addProject(project: Project) {
        val values = ContentValues()
        values.put(BrickListContract.Inventories.COLUMN_NAME_NAME, project.name)
        values.put(BrickListContract.Inventories.COLUMN_NAME_ACTIVE, project.active)
        values.put(BrickListContract.Inventories.COLUMN_NAME_LAST_ACCESSED, project.lastAccessed)
        val db = this.writableDatabase
        db.insert(BrickListContract.Inventories.TABLE_NAME, null, values)
        db.close()
    }

    fun isProjectWithNameInDatabase(projectName: String): Boolean {
        val query = "SELECT * FROM ${BrickListContract.Inventories.TABLE_NAME} WHERE ${BrickListContract.Inventories.COLUMN_NAME_NAME} LIKE \"$projectName\""
        val db = writableDatabase
        val cursor = db.rawQuery(query, null)

        val moveToFirst = cursor.moveToFirst()

        db.close()

        return moveToFirst
    }

}