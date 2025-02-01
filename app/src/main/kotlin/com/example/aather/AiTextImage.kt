package com.example.aather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale

class AiTextImage : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnPhotoAi: Button
    private lateinit var backButton: Button
    private lateinit var btngal: Button
    private lateinit var detectButton: Button
    private lateinit var txtDetector: TextView

    private lateinit var soundButton: Button
    private var textToSpeech: TextToSpeech? = null

    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aitextimage)

        imageView = findViewById(R.id.imagefortxtgeneretion)
        btnPhotoAi = findViewById(R.id.btnPhotoAi)
        btngal = findViewById<Button>(R.id.btngalerry)
        backButton = findViewById<Button>(R.id.backbutton)
        detectButton = findViewById<Button>(R.id.btndetect)
        txtDetector = findViewById(R.id.textgeneretedbyai)
        soundButton = findViewById(R.id.soundbutton)

        intent.getStringExtra("photo_uri")?.let { uriString ->
            val uri = Uri.parse(uriString)
            loadImageFromUri(uri)
        }

        btnPhotoAi.setOnClickListener {
            val intent = Intent(this, Cameraxtry::class.java)
            startActivity(intent)
        }

        btngal.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        detectButton.setOnClickListener {
            if (bitmap == null) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            } else {
                detectTextFromImage(bitmap!!)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        soundButton.setOnClickListener {
            speakText()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadImageFromUri(uri: Uri) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun speakText() {
        val text = txtDetector.text.toString()

        if (text.isBlank()) {
            Toast.makeText(this, "TextView is empty", Toast.LENGTH_SHORT).show()
        } else {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun detectTextFromImage(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                displayText(visionText)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to recognize text: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayText(visionText: Text) {
        if (visionText.text.isEmpty()) {
            Toast.makeText(this, "No text detected", Toast.LENGTH_SHORT).show()
        } else {
            txtDetector.text = visionText.text
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.shutdown()
    }
}

