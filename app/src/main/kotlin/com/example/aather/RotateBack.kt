package com.example.aather

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class RotateBack : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotate_back)

        val textViewChangeRotate = findViewById<TextView>(R.id.textviechangerotate)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            textViewChangeRotate.text = "Normal"
        } else
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
        }
    }

}
