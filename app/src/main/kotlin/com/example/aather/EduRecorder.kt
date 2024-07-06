package com.example.aather

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class EduRecorder : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edurecorder)

        editText = findViewById(R.id.textrecorded)
        val speechToTextBtn = findViewById<Button>(R.id.btnrecorder)
        val downloadBtn = findViewById<Button>(R.id.btndownload)

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(createRecognitionListener())

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechToTextBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        downloadBtn.setOnClickListener {
            downloadText()
        }


    }

    private fun startRecording() {
        isRecording = true
        editText.setText("") // Clear the existing text
        speechRecognizer.startListening(recognizerIntent)
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        isRecording = false
        speechRecognizer.stopListening()
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
    }
    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Invoked when the recognizer is ready to start listening
            }

            override fun onBeginningOfSpeech() {
                // Invoked when the user starts speaking
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Invoked to signal the sound level in the audio stream
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Invoked when more sound has been received
            }

            override fun onEndOfSpeech() {
                // Invoked when the user stops speaking
            }

            override fun onError(error: Int) {
                // Invoked when an error occurs
                if (isRecording) {
                    speechRecognizer.startListening(recognizerIntent)
                }
            }

            override fun onResults(results: Bundle?) {
                // Invoked when recognition results are ready
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    editText.append(matches[0] + " ")
                }
                if (isRecording) {
                    speechRecognizer.startListening(recognizerIntent)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Invoked when partial recognition results are available
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    editText.setText(matches[0])
                    editText.setSelection(editText.text.length)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Reserved for future events
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    private fun downloadText() {
        val text = editText.text.toString()
        if (text.isNotEmpty()) {
            try {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Save text using"))
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to share text", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No text to save", Toast.LENGTH_SHORT).show()
        }
    }
}
