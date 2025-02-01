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
    private var fullText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edurecorder)

        editText = findViewById(R.id.textrecorded)
        val speechToTextBtn = findViewById<Button>(R.id.btnrecorder)
        val backButton = findViewById<Button>(R.id.backbutton)
        val copyButton = findViewById<Button>(R.id.btncopy)
        val downloadButton = findViewById<Button>(R.id.btndownload)
        val trashButton = findViewById<Button>(R.id.btntrash)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(createRecognitionListener())

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechToTextBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        copyButton.setOnClickListener {
            copyTextToClipboard()
        }

        downloadButton.setOnClickListener {
            shareTextToNotes()
        }

        trashButton.setOnClickListener {
            clearEditText()
        }
    }

    private fun startRecording() {
        isRecording = true
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
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
                Toast.makeText(applicationContext, "Error occurred: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    fullText += matches[0] + " "
                    editText.setText(fullText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }
    }

    private fun copyTextToClipboard() {
        val textToCopy = editText.text.toString()

        if (textToCopy.isBlank()) {
            Toast.makeText(this, "No text to copy", Toast.LENGTH_SHORT).show()
            return
        }

        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun shareTextToNotes() {
        val textToShare = editText.text.toString()

        if (textToShare.isBlank()) {
            Toast.makeText(this, "No text to share", Toast.LENGTH_SHORT).show()
            return
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textToShare)
        }

        startActivity(Intent.createChooser(shareIntent, "Share text via"))
    }

    private fun clearEditText() {
        if (editText.text.isBlank()) {
            Toast.makeText(this, "No text to clear", Toast.LENGTH_SHORT).show()
        } else {
            fullText = ""
            editText.text.clear()
            Toast.makeText(this, "Text cleared", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
