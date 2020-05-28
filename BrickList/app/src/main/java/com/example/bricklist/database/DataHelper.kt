package com.example.bricklist.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.bricklist.database.BrickListContract.Codes
import com.example.bricklist.database.BrickListContract.Inventories
import com.example.bricklist.database.BrickListContract.InventoriesParts
import com.example.bricklist.database.BrickListContract.ItemTypes
import com.example.bricklist.database.BrickListContract.Parts
import com.example.bricklist.database.model.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DataHelper(private val context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private var db: SQLiteDatabase? = null

    companion object {
        private const val DB_NAME = "BrickList.db"
        private const val DB_VERSION = 111
    }

    fun openDatabase() {
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

        db = writableDatabase
    }

    override fun close() {
        super.close()
        if (db != null)
            db!!.close()
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

    fun addInventory(inventory: InventoryTO) {
        val values = ContentValues()
        values.put(Inventories.NAME, inventory.name)
        values.put(Inventories.ACTIVE, inventory.active)
        values.put(Inventories.LAST_ACCESSED, inventory.lastAccessed)

        db!!.insert(Inventories.TABLE_NAME, null, values)
    }

    fun findAllInventories(showArchived: Boolean): ArrayList<InventoryTO> {
        var inventory: InventoryTO
        val resultInventoryList = ArrayList<InventoryTO>()

        val db = this.writableDatabase
        val cursor: Cursor?
        cursor = if (!showArchived) {
            db.rawQuery(
                "SELECT * FROM ${Inventories.TABLE_NAME} WHERE ${Inventories.ACTIVE} = 1 ORDER BY ${Inventories.LAST_ACCESSED} DESC",
                null
            )
        } else {
            db.rawQuery("SELECT * FROM ${Inventories.TABLE_NAME}", null)
        }

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                inventory =
                    InventoryTO(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getLong(3)
                    )
                resultInventoryList.add(inventory)
                cursor.moveToNext()
            }

            cursor.close()
        }

        return resultInventoryList
    }

    fun isInventoryWithNameInDatabase(projectName: String): Boolean {
        val query = "SELECT * FROM ${Inventories.TABLE_NAME} WHERE ${Inventories.NAME} = ?"
        val cursor = db!!.rawQuery(query, arrayOf(projectName))
        val moveToFirst = cursor.moveToFirst()
        cursor.close()

        return moveToFirst
    }

    fun findInventoryByName(projectName: String?): InventoryTO {
        val query =
            "SELECT * FROM ${Inventories.TABLE_NAME} WHERE ${Inventories.NAME} = ?"
        val cursor = db!!.rawQuery(query, arrayOf(projectName))

        val project = InventoryTO()
        with(cursor) {
            if (moveToFirst()) {
                project.id =
                    getInt(getColumnIndexOrThrow(Inventories.ID))
                project.name =
                    getString(getColumnIndexOrThrow(Inventories.NAME))
                project.active =
                    getInt(getColumnIndexOrThrow(Inventories.ACTIVE))
                project.lastAccessed =
                    getLong(getColumnIndexOrThrow(Inventories.LAST_ACCESSED))
            }
        }

        return project
    }

    fun addInventoryPart(inventoryPart: InventoryPartTO) {
        val values = ContentValues()
        values.put(InventoriesParts.ID, inventoryPart.id)
        values.put(
            InventoriesParts.INVENTORY_ID,
            inventoryPart.inventoryId
        )
        values.put(InventoriesParts.TYPE_ID, inventoryPart.typeId)
        values.put(InventoriesParts.ITEM_ID, inventoryPart.itemId)
        values.put(
            InventoriesParts.QUANTITY_IN_SET,
            inventoryPart.quantityInSet
        )
        values.put(
            InventoriesParts.QUANTITY_IN_STORE,
            inventoryPart.quantityInStore
        )
        values.put(InventoriesParts.COLOR_ID, inventoryPart.colorId)
        values.put(InventoriesParts.EXTRA, inventoryPart.extra)

        db!!.insert(InventoriesParts.TABLE_NAME, null, values)
    }

    fun activateInventory(projectName: String) {
        val values = ContentValues()
        values.put(Inventories.ACTIVE, 1)

        db!!.update(
            Inventories.TABLE_NAME,
            values,
            "${Inventories.NAME} = ?",
            arrayOf(projectName)
        )
    }

    fun archiveInventory(projectName: String) {
        val values = ContentValues()
        values.put(Inventories.ACTIVE, 0)

        db!!.update(
            Inventories.TABLE_NAME,
            values,
            "${Inventories.NAME} = ?",
            arrayOf(projectName)
        )
    }

    fun updateInventoryPartQuantityInStore(id: Int, value: String) {
        val values = ContentValues()
        values.put(InventoriesParts.QUANTITY_IN_STORE, value)

        db!!.update(
            InventoriesParts.TABLE_NAME,
            values,
            "${InventoriesParts.ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun findMissingInventoryParts(projectName: String): ArrayList<InventoryPartToExportTO> {
        var inventoryPart: InventoryPartToExportTO
        val resultInventoryPartList = ArrayList<InventoryPartToExportTO>()
        val cursor =
            db!!.rawQuery(
                "SELECT ItemTypes.Code, InventoriesParts.ItemID, Colors.Code, InventoriesParts.QuantityInSet, InventoriesParts.QuantityInStore FROM InventoriesParts JOIN ItemTypes ON InventoriesParts.TypeID = ItemTypes.id JOIN Colors ON InventoriesParts.ColorID = Colors.id JOIN Inventories ON InventoriesParts.InventoryID = Inventories.id  WHERE QuantityInStore < QuantityInSet AND Inventories.Name = ?",
                arrayOf(projectName)
            )
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            inventoryPart = InventoryPartToExportTO(
                cursor.getString(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3) - cursor.getInt(4)
            )

            resultInventoryPartList.add(inventoryPart)
            cursor.moveToNext()
        }

        cursor.close()
        return resultInventoryPartList
    }

    fun findInventoryPartsViews(id: Int): ArrayList<InventoryPartViewTO?> {
        var inventoryPart: InventoryPartViewTO
        val resultInventoryPartList = ArrayList<InventoryPartViewTO?>()
        val cursor =
            db!!.rawQuery(
                "SELECT InventoriesParts.ID, InventoryID, InventoriesParts.TypeID, InventoriesParts.ItemID, QuantityInSet, QuantityInStore, InventoriesParts.ColorID, Extra, Parts.Name, Parts.Code, Codes.Image FROM InventoriesParts JOIN Parts ON InventoriesParts.ItemID = Parts.id JOIN Codes ON Parts.id = Codes.ItemID AND InventoriesParts.ColorID = Codes.ColorID WHERE InventoryID = ?",
                arrayOf(id.toString())
            )
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            inventoryPart = InventoryPartViewTO(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getBlob(10)
            )

            resultInventoryPartList.add(inventoryPart)
            cursor.moveToNext()
        }

        cursor.close()
        return resultInventoryPartList
    }

    fun getItemId(code: String): Int {
        val cursor = db!!.rawQuery(
            "SELECT ${Parts.ID} FROM ${Parts.TABLE_NAME} WHERE ${Parts.CODE} = '$code'",
            null
        )

        var id: Int = -1
        cursor.moveToFirst()
        if (cursor.count > 0) {
            id = cursor.getInt(0)
        }

        cursor.close()
        return id
    }

    fun getCodeFromParts(itemId: Int): String {
        var code = ""
        val cursor = db!!.rawQuery(
            "SELECT ${Parts.CODE} FROM ${Parts.TABLE_NAME} WHERE ${Parts.ID} = $itemId",
            null
        )

        if (cursor.count > 0) {
            cursor.moveToFirst()
            code = cursor.getString(0)
        }
        cursor.close()
        return code
    }

    fun addPart(Part: PartTO) {
        val values = ContentValues()
        values.put(Parts.ID, Part.id)
        values.put(Parts.TYPE_ID, Part.typeId)
        values.put(Parts.CODE, Part.code)
        values.put(Parts.CATEGORY_ID, Part.categoryId)
        values.put(Parts.NAME, Part.name)
        values.put(Parts.NAME_PL, Part.namePL)

        db!!.insert(Parts.TABLE_NAME, null, values)
    }

    fun getTypeId(IDString: String): Int {
        val array = arrayOf(IDString)
        val cursor = db!!.rawQuery(
            "SELECT ${ItemTypes.ID} FROM ${ItemTypes.TABLE_NAME} WHERE ${ItemTypes.CODE} = ?",
            array
        )

        var typeID: Int = -1
        cursor.moveToFirst()
        if (cursor.count > 0) {
            typeID = cursor.getInt(0)
        }

        cursor.close()
        return typeID
    }

    fun getLastInventoryId(): Int {
        val cursor =
            db!!.rawQuery("SELECT MAX(${Inventories.ID}) FROM ${Inventories.TABLE_NAME}", null)

        cursor.moveToFirst()
        val maxId: Int = cursor.getInt(0)
        cursor.close()
        return maxId
    }

    fun getLastInventoryPartId(): Int {
        val cursor = db!!.rawQuery(
            "SELECT MAX(${InventoriesParts.ID}) FROM ${InventoriesParts.TABLE_NAME}",
            null
        )

        cursor.moveToFirst()
        val maxId: Int = cursor.getInt(0)
        cursor.close()

        return maxId
    }

    private fun getBrickCodeForImage(itemID: Int, colorID: Int): Int {
        val cursor = db!!.rawQuery(
            "SELECT ${Codes.CODE} FROM ${Codes.TABLE_NAME} WHERE ${Codes.ITEM_ID} = ? AND ${Codes.COLOR_ID} = ?",
            arrayOf(itemID.toString(), colorID.toString())
        )
        var code: Int = -1

        cursor.moveToFirst()
        if (cursor.count > 0) {
            code = cursor.getInt(0)
            cursor.close()
        } else {
            cursor.close()
            val values = ContentValues()
            values.put(Codes.ITEM_ID, itemID)
            values.put(Codes.COLOR_ID, colorID)
            values.put(Codes.CODE, itemID)

            db!!.insert(Codes.TABLE_NAME, null, values)
        }

        return code
    }

    private fun ifImageExists(code: Int): Boolean {
        val cursor = db!!.rawQuery(
            "SELECT ${Codes.IMAGE} FROM ${Codes.TABLE_NAME} WHERE ${Codes.CODE} = ?",
            arrayOf(code.toString())
        )

        cursor.moveToFirst()

        return if (cursor.count > 0 && cursor.getBlob(0) != null) {
            cursor.close()
            true
        } else {
            cursor.close()
            false
        }
    }

    fun downloadAndAddImage(itemID: Int, colorID: Int) {
        val code: Int = getBrickCodeForImage(itemID, colorID)
        if (!ifImageExists(code)) {
            val response = khttp.get(
                url = "https://www.lego.com/service/bricks/5/2/$code"
            )

            if (response.statusCode == 200) {
                addImage(code, response.content)
            } else {
                val secondTryResponse = khttp.get(
                    url = "http://img.bricklink.com/P/$colorID/${getCodeFromParts(itemID)}.jpg"
                )
                if (secondTryResponse.statusCode == 200) {
                    addImage(code, secondTryResponse.content)
                } else {
                    val thirdTryResponse = khttp.get(
                        url = "http://img.bricklink.com/P/$colorID/${getCodeFromParts(itemID)}.gif"
                    )
                    if (thirdTryResponse.statusCode == 200) {
                        addImage(code, thirdTryResponse.content)
                    } else {
                        val fourthTryResponse = khttp.get(
                            url = "http://https://www.bricklink.com/PL/${getCodeFromParts(itemID)}.jpg"
                        )
                        if (fourthTryResponse.statusCode == 200) {
                            addImage(code, fourthTryResponse.content)
                        } else {
                            val fifthTryResponse = khttp.get(
                                url = "http://https://www.bricklink.com/MN/${getCodeFromParts(itemID)}.png"
                            )
                            if (fifthTryResponse.statusCode == 200) {
                                addImage(code, fifthTryResponse.content)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun addImage(code: Int, byteArray: ByteArray) {
        val values = ContentValues()

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val scaledBitmap =
            Bitmap.createScaledBitmap(bitmap, 120, 120 / (bitmap.width / bitmap.height), false)
        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)

        values.put(Codes.IMAGE, stream.toByteArray())
        db!!.update(Codes.TABLE_NAME, values, "${Codes.CODE} = ?", arrayOf(code.toString()))
    }

    fun getImageOfBrick(itemID: Int, colorID: Int): Bitmap? {
        val code: Int = getBrickCodeForImage(itemID, colorID)

        if (code != -1) {
            val cursor = db!!.rawQuery(
                "SELECT ${Codes.IMAGE} FROM ${Codes.TABLE_NAME} WHERE ${Codes.CODE} = ?",
                arrayOf(code.toString())
            )
            val bMap: Bitmap

            cursor.moveToFirst()

            return if (cursor.getBlob(0) != null && cursor.getBlob(0).isNotEmpty()) {
                bMap = BitmapFactory.decodeByteArray(cursor.getBlob(0), 0, cursor.getBlob(0).size)
                cursor.close()
                bMap
            } else {
                cursor.close()
                null
            }
        } else {
            return null
        }
    }

    fun getNameOfBrick(itemID: Int, colorID: Int): String {
        val cursorCode = db!!.rawQuery(
            "SELECT ${Parts.CODE} FROM ${Parts.TABLE_NAME} WHERE ${Parts.ID} = ?",
            arrayOf(itemID.toString())
        )

        val name: String

        if (cursorCode.count > 0) {
            cursorCode.moveToFirst()
            val code: String = cursorCode.getString(0)

            val cursorName = db!!.rawQuery(
                "SELECT ${Parts.NAME} FROM ${Parts.TABLE_NAME} WHERE ${Parts.CODE} = ?",
                arrayOf(code)
            )
            cursorName.moveToFirst()
            name = cursorName.getString(0)
            cursorName.close()
        } else {
            name = "Item ID = $itemID, ColorID = $colorID"
        }

        cursorCode.close()
        return name
    }

    fun deleteInventory(projectName: String?) {
        val cursor = db!!.rawQuery(
            "SELECT ${Inventories.ID} FROM ${Inventories.TABLE_NAME} WHERE ${Inventories.NAME} = ?",
            arrayOf(projectName)
        )

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            db!!.delete(Inventories.TABLE_NAME, "${Inventories.ID} = ?", arrayOf(id.toString()))
            cursor.close()

            val secondCursor = db!!.rawQuery(
                "SELECT ${InventoriesParts.ID} FROM ${InventoriesParts.TABLE_NAME} WHERE ${InventoriesParts.INVENTORY_ID} = ?",
                null
            )

            if (secondCursor.moveToFirst()) {
                while (!secondCursor.isAfterLast) {
                    val partId = secondCursor.getInt(0)
                    db!!.delete(
                        InventoriesParts.TABLE_NAME,
                        "${InventoriesParts.ID} = ?",
                        arrayOf(partId.toString())
                    )
                    secondCursor.close()

                    cursor.moveToNext()
                }

                cursor.close()
            }
        }
    }
}