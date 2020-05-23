package com.example.bricklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.bricklist.database.BrickListContract
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
        var inflater = menuInflater
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

        val dbHelper = DataHelper(this)
        val db = dbHelper.openDatabase()

        val projection = arrayOf(
            BrickListContract.Inventories.COLUMN_NAME_ID,
            BrickListContract.Inventories.COLUMN_NAME_NAME,
            BrickListContract.Inventories.COLUMN_NAME_ACTIVE,
            BrickListContract.Inventories.COLUMN_NAME_LAST_ACCESSED
        )

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val showArchived = sharedPreferences.getBoolean("showArchived", false)

        var selection: String? = null

        if (!showArchived) {
            selection = "${BrickListContract.Inventories.COLUMN_NAME_ACTIVE} = 1"
        }


        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${BrickListContract.Inventories.COLUMN_NAME_LAST_ACCESSED} DESC"

        val cursor = db.query(
            BrickListContract.Inventories.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val projects = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val projectName =
                    getString(getColumnIndexOrThrow(BrickListContract.Inventories.COLUMN_NAME_NAME))
                projects.add(projectName)
            }
        }

        db.close()

        for (project in projects) {
            val rowLayout = layoutInflater.inflate(R.layout.main_item_row, layout, false)
            val textView: TextView = rowLayout.findViewById(R.id.mainProjectName)

            textView.text = project

            layout.addView(rowLayout)
        }
    }

    fun openProject(view: View){
        val projectName = view.findViewById<TextView>(R.id.mainProjectName).text.toString()

        val intent = Intent(this, ProjectActivity::class.java)
        intent.putExtra("EXTRA_PROJECT_NAME", projectName);
        startActivity(intent)
    }
}
