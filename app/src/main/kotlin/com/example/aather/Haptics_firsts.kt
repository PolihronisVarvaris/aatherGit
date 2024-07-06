package com.example.aather

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Haptics_firsts : AppCompatActivity() {

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_haptics_firsts)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val startButton: Button = findViewById(R.id.StartButtonHaptic)
        startButton.setOnClickListener {
            startVibrating()
        }

        val stopButton: Button = findViewById(R.id.StopButtonHaptic)
        stopButton.setOnClickListener {
            stopVibrating()
        }

        val hapticButton: Button = findViewById(R.id.Haptic_effect)
        hapticButton.setOnClickListener {
            //startHapticEffect()
            //it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            //vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
//            val timings: LongArray = longArrayOf(
//                25, 25, 50, 25, 25, 25, 25, 25, 25, 25, 75, 25, 25,
//                300, 25, 25, 150, 25, 25, 25
//            )
//            val amplitudes: IntArray = intArrayOf(
//                38, 77, 79, 84, 92, 99, 121, 143, 180, 217, 255, 170, 85,
//                0, 85, 170, 255, 170, 85, 0
//            )
//            val repeatIndex = -1 // Do not repeat.
//
//            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                    .compose()

                vibrator.vibrate(effect)
            } else {
                // Handle devices running older Android versions (pre-Oreo)
                // You can use a simpler vibration pattern here if needed
            }

        }


    }

    private fun startVibrating() {
        val timings: LongArray = longArrayOf(50, 50, 100, 50, 50)
        val amplitudes: IntArray = intArrayOf(64, 128, 255, 128, 64)
        val repeat = 1
        val repeatingEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat)

        vibrator.vibrate(repeatingEffect)
    }

    private fun stopVibrating() {
        vibrator.cancel()
    }

    private fun startHapticEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && vibrator.hasAmplitudeControl()) {
            //val vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            //val vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            Log.d("Vibration", "before")
            val vibrationEffect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                .compose()
            Log.d("Vibration", "after")
            try {
                vibrator.vibrate(vibrationEffect)

                Log.d("Vibration","Vibration effect: $vibrationEffect")

            } catch (e: Exception) {
                Log.d("Vibration", "Device STUCK on Exception")
            }
        } else {
            // Fallback to a vibration effect compatible with lower API levels
            Log.d("Vibration", "Device does not support composition primitives")
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}