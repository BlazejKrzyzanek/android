package com.example.bricklist

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bricklist.database.DataHelper
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class SaveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        val projectName = intent.getStringExtra("EXTRA_PROJECT_NAME")

        title = "Save $projectName"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initComponents() {
        val filenameView = findViewById<TextView>(R.id.saveFileName)
        filenameView.text = intent.getStringExtra("EXTRA_PROJECT_NAME")

        val radioGroup = findViewById<RadioGroup>(R.id.conditionGroup)

        val buttonSend = findViewById<Button>(R.id.send)
        buttonSend.setOnClickListener {
            val filename = filenameView.text.toString()

            if (filename.isNotEmpty()) {
                val filenameWithoutExtension = filename.split("\\.")[0]

                val condition = when (radioGroup.checkedRadioButtonId) {
                    R.id.radioUsed -> {
                        "U"
                    }
                    R.id.radioNew -> {
                        "N"
                    }
                    else -> null
                }
                writeXml(filenameWithoutExtension, condition)
            } else {
                Toast.makeText(this, getString(R.string.filenameAndEmail), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun writeXml(filenameWithoutExtension: String, condition: String?) {
        val dataHelper = DataHelper(this)
        dataHelper.openDatabase()
        val inventoryPartsToExport =
            dataHelper.findMissingInventoryParts(intent.getStringExtra("EXTRA_PROJECT_NAME"))

        val documentBuilder: DocumentBuilder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document: Document = documentBuilder.newDocument()
        val rootElement: Element = document.createElement("INVENTORY")

        for (part in inventoryPartsToExport) {
            val item: Element = document.createElement("ITEM")
            val itemType: Element = document.createElement("ITEMTYPE")
            itemType.appendChild(document.createTextNode(part.itemType))
            item.appendChild(itemType)

            val itemId: Element = document.createElement("ITEMID")
            itemId.appendChild(document.createTextNode(part.itemId.toString()))
            item.appendChild(itemId)

            val color: Element = document.createElement("COLOR")
            color.appendChild(document.createTextNode(part.color.toString()))
            item.appendChild(color)

            val qTyFilled: Element = document.createElement("QTYFILLED")
            qTyFilled.appendChild(document.createTextNode(part.qtyFilled.toString()))
            item.appendChild(qTyFilled)

            if (condition != null) {
                val conditionElement: Element = document.createElement("CONDITION")
                conditionElement.appendChild(document.createTextNode(condition))
                item.appendChild(conditionElement)
            }

            rootElement.appendChild(item)
        }

        document.appendChild(rootElement)
        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        val path = this.getExternalFilesDir(null)
        val outDir = File(path, "SavedInventories")
        outDir.mkdir()
        val file = File(outDir, "$filenameWithoutExtension.xml")

        transformer.transform(DOMSource(document), StreamResult(file))

        val toast = Toast.makeText(
            applicationContext,
            "XML generated: " + file.absolutePath,
            Toast.LENGTH_LONG
        )
        toast.show()
    }


}