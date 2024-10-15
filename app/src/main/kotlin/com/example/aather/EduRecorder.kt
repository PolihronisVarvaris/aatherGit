package com.example.aather

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class EduRecorder : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private var isRecording = false
    private var fullText = "" // Variable to store the full recognized text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edurecorder)

        editText = findViewById(R.id.textrecorded)
        val speechToTextBtn = findViewById<Button>(R.id.btnrecorder)

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(createRecognitionListener())

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1) // Return only the top result
        }

        speechToTextBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    private fun startRecording() {
        isRecording = true
        // No need to clear the existing text anymore
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
                Toast.makeText(applicationContext, "Error occurred: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    // Append the new recognized text to the existing fullText
                    fullText += matches[0] + " "
                    editText.setText(fullText) // Update the EditText with the new full text
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    // Optionally handle partial results
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
}


