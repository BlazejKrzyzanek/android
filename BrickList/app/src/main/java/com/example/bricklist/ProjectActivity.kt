package com.example.bricklist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class ProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        val projectName = intent.getStringExtra("EXTRA_PROJECT_NAME")

        title = projectName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}