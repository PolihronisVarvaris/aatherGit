package com.example.aather.service


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.aather.ui.CloseActivity

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ComunicationServiceReceiver : BroadcastReceiver() {

    @Inject lateinit var serviceRepository: ComunicationServiceRepository
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ACTION_EXIT"){
            //we want to exit the whole application
            serviceRepository.stopService()
            context?.startActivity(Intent(context,CloseActivity::class.java))

        }

    }
}