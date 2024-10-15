package com.example.aather

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.TouchImageView

class Size_image : AppCompatActivity() {

    private lateinit var imageView: TouchImageView
    private lateinit var btnPhotoZoom: Button
    private lateinit var btnGalleryPreview: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sizezoom)

        imageView = findViewById(R.id.imageviwzoom)
        btnPhotoZoom = findViewById(R.id.imageforzoombutton)
        btnGalleryPreview = findViewById(R.id.previesfromgallery)
        val backButton = findViewById<Button>(R.id.backbutton)

        // Handle capturing a new photo
        btnPhotoZoom.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        // Handle picking an image from the gallery
        btnGalleryPreview.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_GALLERY)
        }

        // Back button functionality
        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        imageView.setImageBitmap(it)
                    }
                }
                REQUEST_IMAGE_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        imageView.setImageURI(it)
                    }
                }
            }
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_GALLERY = 2
    }
}