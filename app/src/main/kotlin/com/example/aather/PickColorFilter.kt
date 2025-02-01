    package com.example.aather

    import android.content.Intent
    import android.database.Cursor
    import android.graphics.Color
    import android.net.Uri
    import android.os.Bundle
    import android.provider.MediaStore
    import android.view.MotionEvent
    import android.view.View
    import android.widget.Button
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.graphics.drawable.toBitmap

    class PickColorFilter : AppCompatActivity() {

        private var isMenuVisible = false
        private lateinit var btnPhoto: Button
        private lateinit var imageholdfilter: ImageView
        private lateinit var colorView: View
        private lateinit var resultTv: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_color_picker)

            imageholdfilter = findViewById(R.id.imageholdfilter)
            colorView = findViewById(R.id.normalvision)
            resultTv = findViewById(R.id.normalvision)
            val menuImageList = findViewById<ImageView>(R.id.menomagelist)
            val menuButton = findViewById<Button>(R.id.menulistdropbutton)
            btnPhoto = findViewById(R.id.camerabuttoncolor)
            val backButton = findViewById<Button>(R.id.backbutton)


            loadLastImage()

            btnPhoto.setOnClickListener {
                val intent = Intent(this, CameraPicker::class.java)
                startActivity(intent)
            }


            imageholdfilter.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                        val imageView = v as ImageView
                        val drawable = imageView.drawable ?: return@setOnTouchListener false
                        val bitmap = drawable.toBitmap()


                        val imageViewWidth = imageView.width
                        val imageViewHeight = imageView.height


                        val imageWidth = bitmap.width
                        val imageHeight = bitmap.height


                        val scaleX = imageWidth.toFloat() / imageViewWidth.toFloat()
                        val scaleY = imageHeight.toFloat() / imageViewHeight.toFloat()


                        val x = (event.x * scaleX).toInt()
                        val y = (event.y * scaleY).toInt()


                        if (x >= 0 && x < bitmap.width && y >= 0 && y < bitmap.height) {
                            val pixel = bitmap.getPixel(x, y)
                            val r = Color.red(pixel)
                            val g = Color.green(pixel)
                            val b = Color.blue(pixel)


                            val hex = String.format("#%02X%02X%02X", r, g, b)
                            colorView.setBackgroundColor(Color.rgb(r, g, b))
                            resultTv.text = "RGB: $r, $g, $b\nHex: $hex"

                            val deuteranomalyColor = simulateDeuteranomalyColor(r, g, b)
                            val deuteranomalyHex = String.format("#%02X%02X%02X", Color.red(deuteranomalyColor), Color.green(deuteranomalyColor), Color.blue(deuteranomalyColor))

                            val protanomalyColor = simulateProtanomalyColor(r, g, b)
                            val protanomalyHex = String.format("#%02X%02X%02X", Color.red(protanomalyColor), Color.green(protanomalyColor), Color.blue(protanomalyColor))

                            val protanopiaColor = simulateProtanopiaColor(r, g, b)
                            val protanopiaHex = String.format("#%02X%02X%02X", Color.red(protanopiaColor), Color.green(protanopiaColor), Color.blue(protanopiaColor))

                            val deuteranopiaColor = simulateDeuteranopiaColor(r, g, b)
                            val deuteranopiaHex = String.format("#%02X%02X%02X", Color.red(deuteranopiaColor), Color.green(deuteranopiaColor), Color.blue(deuteranopiaColor))

                            val tritanomalyColor = simulateTritanomalyColor(r, g, b)
                            val tritanomalyHex = String.format("#%02X%02X%02X", Color.red(tritanomalyColor), Color.green(tritanomalyColor), Color.blue(tritanomalyColor))

                            val tritanopiaColor = simulateTritanopiaColor(r, g, b)
                            val tritanopiaHex = String.format("#%02X%02X%02X", Color.red(tritanopiaColor), Color.green(tritanopiaColor), Color.blue(tritanopiaColor))


                            val deuteranomalyTextView = findViewById<TextView>(R.id.deuteranomaly)
                            deuteranomalyTextView.text = "$deuteranomalyHex"
                            deuteranomalyTextView.setBackgroundColor(deuteranomalyColor)


                            val protanomalyTextView = findViewById<TextView>(R.id.protanomaly)
                            protanomalyTextView.text = "$protanomalyHex"
                            protanomalyTextView.setBackgroundColor(protanomalyColor)


                            val protanopiaTextView = findViewById<TextView>(R.id.protanopia)
                            protanopiaTextView.text = "$protanopiaHex"
                            protanopiaTextView.setBackgroundColor(protanopiaColor)


                            val deuteranopiaTextView = findViewById<TextView>(R.id.deuteranopia)
                            deuteranopiaTextView.text = "$deuteranopiaHex"
                            deuteranopiaTextView.setBackgroundColor(deuteranopiaColor)


                            val tritanomalyTextView = findViewById<TextView>(R.id.tritanomaly)
                            tritanomalyTextView.text = "$tritanomalyHex"
                            tritanomalyTextView.setBackgroundColor(tritanomalyColor)


                            val tritanopiaTextView = findViewById<TextView>(R.id.tritanopia)
                            tritanopiaTextView.text = "$tritanopiaHex"
                            tritanopiaTextView.setBackgroundColor(tritanopiaColor)
                        }
                        true
                    }
                    else -> false
                }
            }

            menuButton.setOnClickListener {
                if (isMenuVisible) {
                    menuImageList.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction {
                            menuImageList.visibility = View.INVISIBLE
                        }
                } else {
                    menuImageList.visibility = View.VISIBLE
                    menuImageList.alpha = 0f
                    menuImageList.animate()
                        .alpha(1f)
                        .setDuration(500)
                }
                isMenuVisible = !isMenuVisible
            }

            backButton.setOnClickListener {
                finish()
            }
        }

        private fun loadLastImage() {
            val imageUri = getLastImageFromGallery()
            imageUri?.let {
                imageholdfilter.setImageURI(it)
            } ?: Toast.makeText(this, "No images found in gallery", Toast.LENGTH_SHORT).show()
        }

        private fun getLastImageFromGallery(): Uri? {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val cursor: Cursor? = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val id = it.getLong(idColumn)
                    return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                }
            }
            return null
        }

        private fun simulateDeuteranomalyColor(r: Int, g: Int, b: Int): Int {
            val rPrime = 0.8 * r + 0.2 * g + 0.0 * b
            val gPrime = 0.258 * r + 0.742 * g + 0.0 * b
            val bPrime = 0.0 * r + 0.142 * g + 0.858 * b

            val red = rPrime.coerceIn(0.0, 255.0).toInt()
            val green = gPrime.coerceIn(0.0, 255.0).toInt()
            val blue = bPrime.coerceIn(0.0, 255.0).toInt()

            return Color.rgb(red, green, blue)
        }

        private fun simulateProtanomalyColor(r: Int, g: Int, b: Int): Int {
            val rPrime = 0.817 * r + 0.183 * g + 0.0 * b
            val gPrime = 0.333 * r + 0.667 * g + 0.0 * b
            val bPrime = 0.0 * r + 0.125 * g + 0.875 * b

            val red = rPrime.coerceIn(0.0, 255.0).toInt()
            val green = gPrime.coerceIn(0.0, 255.0).toInt()
            val blue = bPrime.coerceIn(0.0, 255.0).toInt()

            return Color.rgb(red, green, blue)
        }

        private fun simulateProtanopiaColor(r: Int, g: Int, b: Int): Int {
            val rPrime = 0.567 * r + 0.433 * g + 0.0 * b
            val gPrime = 0.558 * r + 0.442 * g + 0.0 * b
            val bPrime = 0.0 * r + 0.242 * g + 0.758 * b

            val red = rPrime.coerceIn(0.0, 255.0).toInt()
            val green = gPrime.coerceIn(0.0, 255.0).toInt()
            val blue = bPrime.coerceIn(0.0, 255.0).toInt()

            return Color.rgb(red, green, blue)
        }

        private fun simulateDeuteranopiaColor(r: Int, g: Int, b: Int): Int {
            val rPrime = 0.625 * r + 0.375 * g + 0.0 * b
            val gPrime = 0.7 * r + 0.3 * g + 0.0 * b
            val bPrime = 0.0 * r + 0.3 * g + 0.7 * b

            val red = rPrime.coerceIn(0.0, 255.0).toInt()
            val green = gPrime.coerceIn(0.0, 255.0).toInt()
            val blue = bPrime.coerceIn(0.0, 255.0).toInt()

            return Color.rgb(red, green, blue)
        }

        private fun simulateTritanomalyColor(r: Int, g: Int, b: Int): Int {
            val rPrime = 0.967 * r + 0.033 * g + 0.0 * b
            val gPrime = 0.0 * r + 0.733 * g + 0.267 * b
            val bPrime = 0.0 * r + 0.183 * g + 0.817 * b

            val red = rPrime.coerceIn(0.0, 255.0).toInt()
            val green = gPrime.coerceIn(0.0, 255.0).toInt()
            val blue = bPrime.coerceIn(0.0, 255.0).toInt()

            return Color.rgb(red, green, blue)
        }

        private fun simulateTritanopiaColor(r: Int, g: Int, b: Int): Int {
            val rPrime = 0.95 * r + 0.05 * g + 0.0 * b
            val gPrime = 0.0 * r + 0.433 * g + 0.567 * b
            val bPrime = 0.0 * r + 0.475 * g + 0.525 * b

            val red = rPrime.coerceIn(0.0, 255.0).toInt()
            val green = gPrime.coerceIn(0.0, 255.0).toInt()
            val blue = bPrime.coerceIn(0.0, 255.0).toInt()

            return Color.rgb(red, green, blue)
        }

    }