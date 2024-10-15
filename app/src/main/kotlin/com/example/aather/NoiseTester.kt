package com.example.aather

import android.content.Context
import android.media.MediaRecorder
import android.os.*
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import kotlin.math.log10
import kotlin.math.roundToInt

class NoiseTester : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private lateinit var startButton: Button
    private lateinit var volumeText: TextView
    private lateinit var averageVolumeText: TextView
    private lateinit var minVolumeText: TextView
    private lateinit var maxVolumeText: TextView
    private lateinit var shakeButton: Button
    private lateinit var intensitySeekBar: SeekBar
    private var volumes: MutableList<Int> = mutableListOf()
    private lateinit var vibrator: Vibrator
    private val handler = Handler(Looper.getMainLooper())

    private var averageVolume = 0
    private var hapticIntensity = 1 // Default haptic intensity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noise_tester)
        val backButton = findViewById<Button>(R.id.backbutton)

        startButton = findViewById(R.id.startButton)
        shakeButton = findViewById(R.id.shakeButton)
        volumeText = findViewById(R.id.volumeText)
        averageVolumeText = findViewById(R.id.averageVolumeText)
        minVolumeText = findViewById(R.id.minVolumeText)
        maxVolumeText = findViewById(R.id.maxVolumeText)
        intensitySeekBar = findViewById(R.id.intensitySeekBar)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        startButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
                startDecibelMeter()
            }
        }

        shakeButton.setOnClickListener {
            startContinuousVibration()
        }

        intensitySeekBar.max = 10 // Assuming the intensity scale is from 1 to 10
        intensitySeekBar.progress = hapticIntensity
        intensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                hapticIntensity = progress.coerceIn(1, 10)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        backButton.setOnClickListener {
            finish()  // Finish the current activity and go back to the previous one
        }
    }

    private fun startRecording() {
        // Clear the volumes list
        volumes.clear()

        val audioFile = File(externalCacheDir, "temp_audio.3gp")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
                startButton.text = "Stop Recording"
                volumeText.text = "Recording..."
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
        startButton.text = "Start Recording"
        volumeText.text = "Stopped"
        displayStatistics()
        stopContinuousVibration()

        // Reset the average volume
        averageVolume = 0
    }

    private fun startDecibelMeter() {
        handler.post(object : Runnable {
            override fun run() {
                if (isRecording) {
                    val amplitude = mediaRecorder?.maxAmplitude ?: 0
                    val volumeInDb = (20 * log10(amplitude.toDouble())).roundToInt().coerceIn(0, 130)
                    volumeText.text = "Volume: $volumeInDb dB"
                    volumes.add(volumeInDb)

                    // Calculate the average volume
                    averageVolume = volumes.average().toInt()
                    averageVolumeText.text = "Average Volume: $averageVolume dB"

                    // Adjust the vibration power to be within the range of 0-260 (scaled from 0-130 dB)
                    val vibrationPower = averageVolume.coerceIn(0, 130) * hapticIntensity * 2
                    updateVibrationPower(vibrationPower)

                    handler.postDelayed(this, 100) // Update every 100 milliseconds
                }
            }
        })
    }

    private fun displayStatistics() {
        val positiveVolumes = volumes.filter { it >= 0 } // Filter out negative volumes

        if (positiveVolumes.isNotEmpty()) {
            val averageVolume = positiveVolumes.average().toInt()

            // Filter out volumes that are too far from the average
            val filteredVolumes = positiveVolumes.filter { it in averageVolume - 12..averageVolume + 12 }

            val minVolume = filteredVolumes.minOrNull() ?: 0
            val maxVolume = filteredVolumes.maxOrNull() ?: 0

            averageVolumeText.text = "Average Volume: $averageVolume dB"
            minVolumeText.text = "Min Volume: $minVolume dB"
            maxVolumeText.text = "Max Volume: $maxVolume dB"
        } else {
            // If there are no positive volumes, display default values
            averageVolumeText.text = "Average Volume: 0 dB"
            minVolumeText.text = "Min Volume: 0 dB"
            maxVolumeText.text = "Max Volume: 0 dB"
        }
    }

    private fun startContinuousVibration() {
        val maxAmplitude = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.DEFAULT_AMPLITUDE
        } else {
            // For SDK versions below O, use the maximum amplitude
            255
        }

        val vibrationEffect = VibrationEffect.createOneShot(100, maxAmplitude)
        vibrator.vibrate(vibrationEffect)
    }

    private fun updateVibrationPower(power: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = LongArray(100) { 10 } // 100 segments, each 10 ms
            val amplitudes = IntArray(100) { power } // Update with current power
            val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, 0)
            vibrator.vibrate(vibrationEffect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(power.toLong())
        }
    }

    private fun stopContinuousVibration() {
        vibrator.cancel()
    }
}