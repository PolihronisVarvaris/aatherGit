package com.example.aather

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.Toast
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SplashActivity : AppCompatActivity(), SensorEventListener {

    private var vibrateButtonClicked = false
    private lateinit var vibrator: Vibrator
    private lateinit var textToSpeech: TextToSpeech
    private var announceButtonClicked = false

    // Shake detection variables
    private lateinit var sensorManager: SensorManager
    private var lastShakeTimestamp: Long = 0
    private var shakeThreshold = 800

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val backButton = findViewById<Button>(R.id.backbutton)
        val shakeButton = findViewById<Button>(R.id.shakebutton)
        val vibrateButton = findViewById<Button>(R.id.vibrateButton)
        val rotateButton = findViewById<Button>(R.id.rotatebutton)
        val announceButton = findViewById<Button>(R.id.announcebutton)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = textToSpeech.setLanguage(Locale.US)
            } else {
                showToast("TextToSpeech Initialization failed")
            }
        })

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        backButton.setOnClickListener {
            showToast("Back button clicked")
            addCheckmark(backButton)
        }

        shakeButton.setOnClickListener {
            showToast("Shake button clicked")
            addCheckmark(shakeButton)
        }

        vibrateButton.setOnClickListener {
            if (!vibrateButtonClicked) {
                vibrateStrong()
                addCheckmark(vibrateButton)
                vibrateButtonClicked = true
            } else {
                vibrateLow()
                removeCheckmark(vibrateButton)
                vibrateButtonClicked = false
            }
        }

        rotateButton.setOnClickListener {
            showToast("Rotate button clicked")
            addCheckmark(rotateButton)
        }

        announceButton.setOnClickListener {
            if (!announceButtonClicked) {
                announceText("Announce Button Activate")
                addCheckmark(announceButton)
                announceButtonClicked = true
            } else {
                announceText("Announce Button Deactivate")
                removeCheckmark(announceButton)
                announceButtonClicked = false
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun addCheckmark(button: Button) {
        if (!button.text.contains("✔")) {
            button.text = "${button.text} ✔"
        }
    }

    private fun removeCheckmark(button: Button) {
        val currentText = button.text.toString()
        if (currentText.contains("✔")) {
            button.text = currentText.replace(" ✔", "")
        }
    }

    private fun vibrateStrong() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, 220))
        } else {
            vibrator.vibrate(500)
        }
    }

    private fun vibrateLow() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, 2))
        } else {
            vibrator.vibrate(50)
        }
    }

    private fun announceText(message: String) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val shakeAcceleration = Math.sqrt((x * x + y * y + z * z).toDouble())

            if (shakeAcceleration > 15) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTimestamp > shakeThreshold) {
                    showToast("Phone shaken!")

                    val shakeButton = findViewById<Button>(R.id.shakebutton)

                    if (shakeButton.text.contains("✔")) {
                        removeCheckmark(shakeButton)
                    } else {
                        addCheckmark(shakeButton)
                    }

                    lastShakeTimestamp = currentTime
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)

        val rotateButton = findViewById<Button>(R.id.rotatebutton)

        if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            addCheckmark(rotateButton)
        } else {
            removeCheckmark(rotateButton)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}
