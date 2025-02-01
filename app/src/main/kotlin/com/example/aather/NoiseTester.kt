package com.example.aather

import android.content.Context
import android.media.MediaRecorder
import android.os.*
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
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
    private lateinit var intensitySeekBar: SeekBar
    private var volumes: MutableList<Int> = mutableListOf()
    private lateinit var vibrator: Vibrator
    private val handler = Handler(Looper.getMainLooper())

    private var averageVolume = 0
    private var hapticIntensity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noise_tester)
        val backButton = findViewById<Button>(R.id.backbutton)

        startButton = findViewById(R.id.startButton)
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


        intensitySeekBar.max = 9
        intensitySeekBar.progress = 0
        intensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                hapticIntensity = (progress * 254/ 10).coerceIn(1, 254)

                val vibrationDuration = (progress * 1000 / 10).coerceIn(1, 1000)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(vibrationDuration.toLong(), hapticIntensity)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(vibrationDuration.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun startRecording() {
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

                    val mappedIntensity = mapVolumeToIntensity(volumeInDb)

                    updateVibrationPower(mappedIntensity)

                    handler.postDelayed(this, 100)
                }
            }
        })
    }


    private fun mapVolumeToIntensity(volumeInDb: Int): Int {
        return when {
            volumeInDb in 40..55 -> {
                1
            }
            volumeInDb in 56..70 -> {

                val intensity = ((volumeInDb - 55) * 100 / 15) + 1
                intensity.coerceIn(1, 10)
            }
            volumeInDb in 71..80 -> {
                val intensity = ((volumeInDb - 70) * 200 / 10) + 100
                intensity.coerceIn(11, 100)
            }
            volumeInDb in 81..90 -> {
                val intensity = ((volumeInDb - 80) * 100 / 10) + 300
                intensity.coerceIn(101, 210)
            }
            volumeInDb in 91..110 -> {
                val intensity = ((volumeInDb - 90) * 80 / 20) + 400
                intensity.coerceIn(211, 254)
            }
            else -> {
                0
            }
        }
    }



    private fun displayStatistics() {
        val positiveVolumes = volumes.filter { it >= 0 }

        if (positiveVolumes.isNotEmpty()) {
            val averageVolume = positiveVolumes.average().toInt()


            val filteredVolumes = positiveVolumes.filter { it in averageVolume - 12..averageVolume + 12 }

            val minVolume = filteredVolumes.minOrNull() ?: 0
            val maxVolume = filteredVolumes.maxOrNull() ?: 0

            averageVolumeText.text = "Average Volume: $averageVolume dB"
            minVolumeText.text = "Min Volume: $minVolume dB"
            maxVolumeText.text = "Max Volume: $maxVolume dB"
        } else {

            averageVolumeText.text = "Average Volume: 0 dB"
            minVolumeText.text = "Min Volume: 0 dB"
            maxVolumeText.text = "Max Volume: 0 dB"
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startContinuousVibration() {
        val vibratePattern = longArrayOf(0, 400, 800, 600, 800, 800, 800, 1000)
        val amplitudes = intArrayOf(0, 50, 0, 100, 0, 150, 0, 255)


        if (vibrator.hasAmplitudeControl()) {
            val effect = VibrationEffect.createWaveform(vibratePattern, amplitudes, -1)
            vibrator.vibrate(effect)
        }
    }

    private fun updateVibrationPower(power: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = LongArray(100) { 10 }
            val amplitudes = IntArray(100) { power }
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