package com.example.aather

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import java.util.Locale

class Speech_to_text : AppCompatActivity() {
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var editText:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech_to_text)


        editText = findViewById<EditText>(R.id.editText)
        val textToSpeechBtn = findViewById<Button>(R.id.textToSpeechBtn)

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Check if English is supported
                val resultEnglish = textToSpeech.setLanguage(Locale.ENGLISH)
                if (resultEnglish == TextToSpeech.LANG_MISSING_DATA || resultEnglish == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "English language is not supported", Toast.LENGTH_LONG).show()
                }

                // Check if Greek is supported
                val resultGreek = textToSpeech.setLanguage(Locale("el", "GR"))
                if (resultGreek == TextToSpeech.LANG_MISSING_DATA || resultGreek == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Greek language is not supported", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Text-to-speech initialization failed", Toast.LENGTH_LONG).show()
            }
        }

        textToSpeechBtn.setOnClickListener {
            if (editText.text.toString().trim().isNotEmpty()) {
                textToSpeech.speak(
                    editText.text.toString().trim(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            } else {
                Toast.makeText(this, "Required", Toast.LENGTH_LONG).show()
            }
        }

        val speechToTextBtn = findViewById<Button>(R.id.speechToTextBtn)
        speechToTextBtn.setOnClickListener {
            editText.text = null
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something")
                result.launch(intent)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if (result.resultCode == Activity.RESULT_OK){
            val results = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            ) as ArrayList<String>

            editText.setText(results[0])
        }
    }
}