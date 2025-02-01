package com.example.aather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.Toast

class PowerButtonReceiver : BroadcastReceiver() {

    private var handler: Handler = Handler()
    private var startTime: Long = 0
    private val longPressDuration: Long = 15000

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            startTime = System.currentTimeMillis()
            handler.postDelayed({
                if (System.currentTimeMillis() - startTime >= longPressDuration) {
                    val sosIntent = Intent(context, SOSbutton::class.java)
                    sosIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(sosIntent)
                    Toast.makeText(context, "SOS triggered!", Toast.LENGTH_SHORT).show()
                }
            }, longPressDuration)
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            handler.removeCallbacksAndMessages(null)
        }
    }
}
