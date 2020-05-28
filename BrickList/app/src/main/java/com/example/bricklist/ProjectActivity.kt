package com.example.bricklist

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bricklist.database.DataHelper
import com.example.bricklist.database.model.InventoryTO


class ProjectActivity : AppCompatActivity() {

    private var inventory: InventoryTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        val projectName = intent.getStringExtra("EXTRA_PROJECT_NAME")

        title = projectName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        displayBricks()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.project_menu, menu)

        if (inventory == null) {
            val dbHelper = DataHelper(this)
            dbHelper.openDatabase()
            inventory = dbHelper.findInventoryByName(intent.getStringExtra("EXTRA_PROJECT_NAME"))
            dbHelper.close()
        }

        if (inventory!!.active == 0) {
            val archive = menu.findItem(R.id.action_archive)
            archive.isVisible = false

            val unarchive = menu.findItem(R.id.action_unarchive)
            unarchive.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                Toast.makeText(this, "SAVE", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.action_remove -> {
                val dbHelper = DataHelper(this)
                dbHelper.openDatabase()
                dbHelper.deleteInventory(intent.getStringExtra("EXTRA_PROJECT_NAME"))
                dbHelper.close()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.action_archive -> {
                val dbHelper = DataHelper(this)
                dbHelper.openDatabase()
                dbHelper.archiveInventory(intent.getStringExtra("EXTRA_PROJECT_NAME"))
                dbHelper.close()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun displayBricks() {
        val layout: LinearLayout = findViewById(R.id.listOfBricks)
        layout.removeAllViews()

        val dbHelper = DataHelper(this)

        dbHelper.openDatabase()
        if (inventory == null)
            inventory = dbHelper.findInventoryByName(intent.getStringExtra("EXTRA_PROJECT_NAME"))
        val inventoryId = inventory!!.id
        val bricks = dbHelper.findInventoryPartsViews(inventoryId)
        for (brick in bricks) {
            val rowLayout = layoutInflater.inflate(R.layout.project_item_row, layout, false)

            val brickImageView: ImageView = rowLayout.findViewById(R.id.imageView)
            val brickNameView: TextView = rowLayout.findViewById(R.id.brickName)
            val brickCodeView: TextView = rowLayout.findViewById(R.id.brickCode)
            val quantityInStoreView: TextView = rowLayout.findViewById(R.id.quantityInStore)
            val quantityInSetView: TextView = rowLayout.findViewById(R.id.quantityInSet)
            val plusButton: Button = rowLayout.findViewById(R.id.plusButton)
            val minusButton: Button = rowLayout.findViewById(R.id.minusButton)

            if (brick.image != null)
                brickImageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        brick.image,
                        0,
                        brick.image!!.size
                    )
                )

            brickNameView.text = brick.name
            brickCodeView.text = brick.code
            quantityInSetView.text = brick.quantityInSet.toString()
            quantityInStoreView.text = brick.quantityInStore.toString()

            plusButton.setOnClickListener {
                val oldValue = quantityInStoreView.text.toString().toInt()
                if (oldValue < 9998) {
                    val newValue =
                        (quantityInStoreView.text.toString().toInt() + 1).toString()
                    quantityInStoreView.text = newValue

                    dbHelper.openDatabase()
                    dbHelper.updateInventoryPartQuantityInStore(brick.id, newValue)
                    dbHelper.close()
                }
            }

            minusButton.setOnClickListener {
                val oldValue = quantityInStoreView.text.toString().toInt()
                if (oldValue > 0) {
                    val newValue =
                        (quantityInStoreView.text.toString().toInt() - 1).toString()
                    quantityInStoreView.text = newValue

                    dbHelper.openDatabase()
                    dbHelper.updateInventoryPartQuantityInStore(brick.id, newValue)
                    dbHelper.close()
                }
            }

            layout.addView(rowLayout)

        }
        dbHelper.close()
    }
}