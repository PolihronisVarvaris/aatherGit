package com.example.aather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.PowerManager

class PowerButtonReceiver : BroadcastReceiver() {
    private var powerButtonPressTime: Long = 0
    private var isPowerButtonPressed = false
    private val handler = Handler()
    private val delayMillis: Long = 5000 // 5 seconds

    override fun onReceive(context: Context?, intent: Intent?) {
//        when (intent?.action) {
//            Intent.ACTION_SCREEN_OFF -> {
//                // Reset flag and time when screen is turned off
//                isPowerButtonPressed = false
//                powerButtonPressTime = 0
//            }
//            Intent.ACTION_SCREEN_ON -> {
//                if (isPowerButtonPressed) {
//                    // If screen is turned on and power button is still pressed, start SOSbutton activity
//                    val currentTime = System.currentTimeMillis()
//                    if (currentTime - powerButtonPressTime >= delayMillis) {
//                        val startIntent = Intent(context, SOSbutton::class.java)
//                        startIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        context?.startActivity(startIntent)
//                    }
//                    // Reset flag and time
//                    isPowerButtonPressed = false
//                    powerButtonPressTime = 0
//                }
//            }
//        }
    }
}
