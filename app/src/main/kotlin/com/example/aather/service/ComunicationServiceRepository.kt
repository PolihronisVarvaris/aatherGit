package com.example.aather.service

import android.content.Context
import android.content.Intent
import android.os.Build
import javax.inject.Inject

class ComunicationServiceRepository @Inject constructor(
    private val context: Context
) {

    fun startService(username:String){
        Thread{
            val intent = Intent(context, ComunicationService::class.java)
            intent.putExtra("username",username)
            intent.action = ComunicationServiceActions.START_SERVICE.name
            startServiceIntent(intent)
        }.start()
    }

    private fun startServiceIntent(intent: Intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent)
        }else{
            context.startService(intent)
        }
    }

    fun setupViews(videoCall: Boolean, caller: Boolean, target: String) {
        val intent = Intent(context,ComunicationService::class.java)
        intent.apply {
            action = ComunicationServiceActions.SETUP_VIEWS.name
            putExtra("isVideoCall",videoCall)
            putExtra("target",target)
            putExtra("isCaller",caller)
        }
        startServiceIntent(intent)
    }

    fun sendEndCall() {
        val intent = Intent(context,ComunicationService::class.java)
        intent.action = ComunicationServiceActions.END_CALL.name
        startServiceIntent(intent)
    }

    fun switchCamera() {
        val intent = Intent(context,ComunicationService::class.java)
        intent.action = ComunicationServiceActions.SWITCH_CAMERA.name
        startServiceIntent(intent)
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        val intent = Intent(context, ComunicationService::class.java)
        intent.action = ComunicationServiceActions.TOGGLE_AUDIO.name
        intent.putExtra("shouldBeMuted",shouldBeMuted)
        startServiceIntent(intent)
    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        val intent = Intent(context, ComunicationService::class.java)
        intent.action = ComunicationServiceActions.TOGGLE_VIDEO.name
        intent.putExtra("shouldBeMuted",shouldBeMuted)
        startServiceIntent(intent)
    }

    fun toggleAudioDevice(type: String) {
        val intent = Intent(context, ComunicationService::class.java)
        intent.action = ComunicationServiceActions.TOGGLE_AUDIO_DEVICE.name
        intent.putExtra("type",type)
        startServiceIntent(intent)
    }

    fun toggleScreenShare(isStarting: Boolean) {
        val intent = Intent(context,ComunicationService::class.java)
        intent.action = ComunicationServiceActions.TOGGLE_SCREEN_SHARE.name
        intent.putExtra("isStarting",isStarting)
        startServiceIntent(intent)
    }

    fun stopService() {
        val intent = Intent(context,ComunicationService::class.java)
        intent.action = ComunicationServiceActions.STOP_SERVICE.name
        startServiceIntent(intent)
    }

}