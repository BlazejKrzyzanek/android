package com.example.bricklist

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.bricklist.database.DataHelper
import com.example.bricklist.database.model.InventoryPartTO
import com.example.bricklist.database.model.InventoryTO
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit


class NewProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun showMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun addClicked(view: View) {
        val projectNumberView: EditText = findViewById(R.id.projectNumber)
        val projectNameView: EditText = findViewById(R.id.projectName)

        if (projectNameView.text.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Project name cannot be empty!",
                Toast.LENGTH_LONG
            ).show()


            return
        }
        if (projectNumberView.text.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Project number cannot be empty!",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val dbHelper = DataHelper(this)
        dbHelper.openDatabase()
        val projectAlreadyExistsName =
            dbHelper.isInventoryWithNameInDatabase(projectNameView.text.toString())
        dbHelper.close()

        if (projectAlreadyExistsName) {
            Toast.makeText(
                this,
                "You've already created project with such name.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val task = BgTask()
            task.execute(projectNumberView.text.toString(), projectNameView.text.toString())

            val result = task.get(150, TimeUnit.SECONDS)

            Toast.makeText(
                this,
                result,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private inner class BgTask() : AsyncTask<String, Int, String>() {

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result.equals("Downloaded")) {
                showMainActivity()
            }
        }

        override fun doInBackground(vararg p0: String?): String {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val baseUrl =
                sharedPreferences.getString("urlPrefix", "http://fcds.cs.put.poznan.pl/MyWeb/BL/")
            val response = khttp.get(
                baseUrl + p0[0] + ".xml"
            )

            return if (response.statusCode == 200) {
                val xmlFile = ByteArrayInputStream(response.text.toByteArray())
                parseXML(xmlFile, p0[1]!!)
                "Downloaded"
            } else {
                "Cant find such project"
            }
        }
    }

    private fun parseXML(input: InputStream, projectName: String) {
        val parserFactory: XmlPullParserFactory? = XmlPullParserFactory.newInstance()
        val parser: XmlPullParser? = parserFactory?.newPullParser()
        parser?.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser?.setInput(input, null)
        processParser(parser!!, projectName)
    }

    private fun processParser(parser: XmlPullParser, projectName: String) {
        val dbHelper = DataHelper(this)
        dbHelper.openDatabase()

        val nextInventoryId = dbHelper.getLastInventoryId() + 1
        var nextInventoryPartId: Int = dbHelper.getLastInventoryPartId() + 1

        var eventType: Int = parser.eventType
        val inventoryParts: ArrayList<InventoryPartTO> = ArrayList()

        val inventory = InventoryTO(nextInventoryId, projectName, 1, System.currentTimeMillis())

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "ITEM" -> {
                        val readItem =
                            readItem(dbHelper, parser, nextInventoryId, nextInventoryPartId++)
                        if (readItem != null)
                            inventoryParts.add(readItem)
                    }
                }
            }

            eventType = parser.next()
        }

        dbHelper.addInventory(inventory)

        DoAsync({
            for (part in inventoryParts) {
                if (part.itemId != -1) {
                    dbHelper.addInventoryPart(part)
                    dbHelper.downloadAndAddImage(
                        part.itemId,
                        part.colorId
                    )
                }
            }
        },
            { dbHelper.close() }
        )
    }

    private fun readItem(
        dbHelper: DataHelper,
        parser: XmlPullParser,
        inventoryId: Int,
        partId: Int
    ): InventoryPartTO? {
        parser.require(XmlPullParser.START_TAG, null, "ITEM")

        val inventoryPartTO = InventoryPartTO()
        inventoryPartTO.id = partId
        inventoryPartTO.inventoryId = inventoryId
        inventoryPartTO.quantityInStore = 0

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "ITEMID" -> {
                        inventoryPartTO.itemId = dbHelper.getItemId(parser.nextText())
                    }
                    "QTY" -> {
                        inventoryPartTO.quantityInSet = parser.nextText().toInt()
                    }
                    "COLOR" -> {
                        inventoryPartTO.colorId = parser.nextText().toInt()
                    }
                    "ITEMTYPE" -> {
                        inventoryPartTO.typeId = dbHelper.getTypeId(parser.nextText())
                    }
                    "ALTERNATE" -> {
                        if (parser.nextText() != "N")
                            return null
                    }
                    else -> skip(parser)
                }
            }
        }

        return inventoryPartTO
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
