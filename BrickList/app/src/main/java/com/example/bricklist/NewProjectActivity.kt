package com.example.bricklist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bricklist.database.DataHelper
import com.example.bricklist.database.model.Project


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

    fun addButton(view: View) {
        val projectNumberView: EditText = findViewById(R.id.projectNumber)
        val projectNameView: EditText = findViewById(R.id.projectName)

        if(projectNameView.text.isNullOrEmpty())
        {
            Toast.makeText(
                this,
                "Project name cannot be empty!",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if(projectNumberView.text.isNullOrEmpty())
        {
            Toast.makeText(
                this,
                "Project number cannot be empty!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val dbHelper = DataHelper(this)

        val projectExistsLego = true
        val projectAlreadyExistsName = dbHelper.isProjectWithNameInDatabase(projectNameView.text.toString())

        if (!projectExistsLego) {
            Toast.makeText(
                this,
                "Project with such number doesn't exist!",
                Toast.LENGTH_LONG
            ).show()
        }
        else if (projectAlreadyExistsName){
            Toast.makeText(
                this,
                "You've already created project with such name.",
                Toast.LENGTH_LONG
            ).show()
        }
        else{
            dbHelper.addProject(
                Project(
                    projectNameView.text.toString(),
                    projectNumberView.text.toString(),
                    1,
                    System.currentTimeMillis().toInt()
                )
            )

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
