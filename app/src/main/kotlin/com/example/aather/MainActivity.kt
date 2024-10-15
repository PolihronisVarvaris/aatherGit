package com.example.aather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import com.example.aather.ui.LoginActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonClick = findViewById<Button>(R.id.button_speact_to_text)
        val buttonshake = findViewById<Button>(R.id.button_shake_detection)
        val buttontry = findViewById<Button>(R.id.button_speacttotext)
        val buttonLogIn = findViewById<Button>(R.id.btn_log_in)
        val buttonRotate = findViewById<Button>(R.id.button_rotate)
        val buttonhHapticsFirst = findViewById<Button>(R.id.haptics_activity)
        val buttonNoiseTester = findViewById<Button>(R.id.noisetester_activity)
        val buttonsos = findViewById<Button>(R.id.sosbutton_activity)
        val buttonmenu = findViewById<Button>(R.id.slidemenu_activity)
        val buttonintro = findViewById<Button>(R.id.button_intro)


        FirebaseDatabase.getInstance().reference.child("masoud").setValue("hello")

        buttonClick.setOnClickListener {
            val intent = Intent(this, Speech_to_text::class.java)
            startActivity(intent)
        }

        buttonintro.setOnClickListener {
            val intent = Intent(this, Intro::class.java)
            startActivity(intent)
        }

        buttonshake.setOnClickListener {
            val intent = Intent(this, Shake_detection::class.java)
            startActivity(intent)
        }

        buttonLogIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        buttontry.setOnClickListener {
            val intent = Intent(this, speechtexttry::class.java)
            startActivity(intent)
        }

        buttonRotate.setOnClickListener {
            val intent = Intent(this, RotateBack::class.java)
            startActivity(intent)
        }

        buttonhHapticsFirst.setOnClickListener {
            val intent = Intent(this, Haptics_firsts::class.java)
            startActivity(intent)
        }

        buttonNoiseTester.setOnClickListener {
            val intent = Intent(this, EduRecorder::class.java)
            startActivity(intent)
        }

        buttonsos.setOnClickListener {
            val intent = Intent(this, SOSbutton::class.java)
            startActivity(intent)
        }

        buttonmenu.setOnClickListener {
            val intent = Intent(this, VolunteerChoice::class.java)
            startActivity(intent)
        }
    }
}
