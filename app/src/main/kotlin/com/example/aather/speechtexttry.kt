package com.example.aather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*

class speechtexttry : AppCompatActivity() {

    // Declare constants outside of any method
    private val REQUEST_CODE_SPEECH_INPUT = 1

    // Declare variables at class level
    private lateinit var outputTV: TextView
    private lateinit var micIV: ImageView

    private val speechRecognitionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val res: ArrayList<String>? = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            res?.let {
                outputTV.text = it[0]
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speechtexttry)

        // Initialize variables with their corresponding views
        outputTV = findViewById(R.id.idTVOutput)
        micIV = findViewById(R.id.idIVMic)

        // Set click listener to the microphone image view
        micIV.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

            // Set supported languages
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString() + ",el-GR") // Add Greek locale

            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            try {
                speechRecognitionLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(this@speechtexttry, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
