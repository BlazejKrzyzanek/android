package com.example.bricklist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.bricklist.database.DataHelper


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayProjects()
    }

    override fun onResume() {
        super.onResume()
        displayProjects()
    }

    fun addProject(v: View) {
        val intent = Intent(this, NewProjectActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }

        return true
    }

    private fun displayProjects() {
        val layout: LinearLayout = findViewById(R.id.listOfProjects)
        layout.removeAllViews()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val showArchived = sharedPreferences.getBoolean("archive", false)

        val dbHelper = DataHelper(this)
        dbHelper.openDatabase()
        val inventories = dbHelper.findAllInventories(showArchived)
        dbHelper.close()

        for (inventory in inventories) {
            val rowLayout = layoutInflater.inflate(R.layout.main_item_row, layout, false)
            val textView: TextView = rowLayout.findViewById(R.id.mainProjectName)
            if (inventory.active == 0) {
                textView.setTextColor(resources.getColor(android.R.color.darker_gray, theme))
            }

            textView.text = inventory.name
            rowLayout.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    rowLayout.setBackgroundColor(
                        resources.getColor(
                            android.R.color.darker_gray,
                            theme
                        )
                    )
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    resources.getColor(android.R.color.white, theme)
                }
                false
            }
            layout.addView(rowLayout)
        }
    }

    fun openProject(view: View) {
        val projectName = view.findViewById<TextView>(R.id.mainProjectName).text.toString()

        val intent = Intent(this, ProjectActivity::class.java)
        intent.putExtra("EXTRA_PROJECT_NAME", projectName);
        startActivity(intent)
    }
}
