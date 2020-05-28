package com.example.bricklist

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager


class SaveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        val projectName = intent.getStringExtra("EXTRA_PROJECT_NAME")

        title = "Save $projectName"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
    }

    private fun initComponents() {
        val filenameView = findViewById<TextView>(R.id.saveFileName)
        filenameView.text = intent.getStringExtra("EXTRA_PROJECT_NAME")

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val defaultEmail = sharedPreferences.getString("defaultEmail", "name@mail.com")

        val emailView = findViewById<TextView>(R.id.email)
        emailView.text = defaultEmail

        val buttonSend = findViewById<Button>(R.id.send)
        buttonSend.setOnClickListener {
            val filename = filenameView.text.toString()
            val emailAddress = emailView.text.toString()

            if (filename.isNotEmpty() && emailAddress.isNotEmpty()) {
                sendFileViaEmail(filename, emailAddress)
            } else {
                Toast.makeText(this, getString(R.string.filenameAndEmail), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun sendFileViaEmail(filename: String, emailAddress: String) {
        val filenameWithoutExtension = filename.split("\\.")[0]


    }


}