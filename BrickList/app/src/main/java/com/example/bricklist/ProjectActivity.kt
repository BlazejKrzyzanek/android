package com.example.bricklist

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bricklist.database.DataHelper
import com.example.bricklist.database.model.InventoryPartViewTO
import com.example.bricklist.database.model.InventoryTO
import kotlinx.android.synthetic.main.activity_project.*


class ProjectActivity : AppCompatActivity() {

    private var inventory: InventoryTO? = null
    private var projectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        projectName = intent.getStringExtra("EXTRA_PROJECT_NAME")

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
            inventory = dbHelper.findInventoryByName(projectName)
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
                val intent = Intent(this, SaveActivity::class.java)
                intent.putExtra("EXTRA_PROJECT_NAME", projectName);
                startActivity(intent)
            }
            R.id.action_remove -> {
                val dbHelper = DataHelper(this)
                dbHelper.openDatabase()
                dbHelper.deleteInventory(projectName)
                dbHelper.close()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.action_archive -> {
                val dbHelper = DataHelper(this)
                dbHelper.openDatabase()
                dbHelper.archiveInventory(projectName!!)
                dbHelper.close()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.action_unarchive -> {
                val dbHelper = DataHelper(this)
                dbHelper.openDatabase()
                dbHelper.activateInventory(projectName!!)
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

        val dbHelper = DataHelper(this)
        dbHelper.openDatabase()
        if (inventory == null) inventory = dbHelper.findInventoryByName(projectName)
        dbHelper.updateLastAccessedInventory(projectName)
        val inventoryId = inventory!!.id
        val bricks = dbHelper.findInventoryPartsViews(inventoryId)
        dbHelper.close()

        val adapter = BricksListAdapter(this, R.layout.project_item_row, bricks)
        this.listOfBricks.adapter = adapter
    }

    class BricksListAdapter(
        context: Context?,
        textViewResourceId: Int,
        items: ArrayList<InventoryPartViewTO?>?
    ) :
        ArrayAdapter<InventoryPartViewTO?>(context!!, textViewResourceId, items!!) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater: LayoutInflater = LayoutInflater.from(context)
            var convertedView = layoutInflater.inflate(R.layout.project_item_row, parent, false)

            val brickImageView: ImageView = convertedView.findViewById(R.id.imageView)
            val brickNameView: TextView = convertedView.findViewById(R.id.brickName)
            val brickCodeView: TextView = convertedView.findViewById(R.id.brickCode)
            val quantityInStoreView: TextView = convertedView.findViewById(R.id.quantityInStore)
            val quantityInSetView: TextView = convertedView.findViewById(R.id.quantityInSet)
            val plusButton: Button = convertedView.findViewById(R.id.plusButton)
            val minusButton: Button = convertedView.findViewById(R.id.minusButton)

            val brick: InventoryPartViewTO? = getItem(position)
            val dbHelper = DataHelper(context)

            if (brick != null) {
                if (brick.image != null) {
                    brickImageView.setImageBitmap(
                        BitmapFactory.decodeByteArray(
                            brick.image,
                            0,
                            brick.image!!.size
                        )
                    )
                } else {
                    DoAsync({
                        dbHelper.openDatabase()
                        brick.image = dbHelper.downloadAndAddImage(brick.itemId, brick.colorId)
                        dbHelper.close()
                    }, {
                        if (brick.image != null)
                            brickImageView.setImageBitmap(
                                BitmapFactory.decodeByteArray(
                                    brick.image,
                                    0,
                                    brick.image!!.size
                                )
                            )
                    })
                }

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
                        brick.quantityInStore = oldValue + 1

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
                        brick.quantityInStore = oldValue - 1

                        dbHelper.openDatabase()
                        dbHelper.updateInventoryPartQuantityInStore(brick.id, newValue)
                        dbHelper.close()
                    }
                }
            }
            return convertedView
        }
    }
}