package com.example.aather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.media.MediaRecorder
import android.os.Environment
import java.io.IOException
import android.provider.MediaStore
import android.content.ContentValues
import android.os.Handler
import java.util.*


class SOSbutton : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val RECORD_AUDIO_REQUEST_CODE = 101
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sosbutton)

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        // Request location permission and send SMS when permission is granted
//        requestLocationPermission()
//
//        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
//        } else {
//            startRecording()
//        }

    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            // Permission is already granted, get user's location and send SMS
            getUserLocationAndSendSMS()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            } else {
                Toast.makeText(this, "Permission denied. Cannot record audio.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserLocationAndSendSMS() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val message = "SOS from the App. Location: $latitude, $longitude"
                    sendSMS("(650) 555-6789", message)
                } ?: run {
                    Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(applicationContext, "SMS sent successfully", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(applicationContext, "Location permission denied", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun startRecording() {
        val audioFilename = "Recording_${UUID.randomUUID()}.mp3"
        val audioFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)?.absolutePath + "/" + audioFilename

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath) // Pass the file path as a String
            try {
                prepare()
                start()
                isRecording = true
                Toast.makeText(this@SOSbutton, "Recording started", Toast.LENGTH_SHORT).show()

                // Stop recording after 30 seconds
                Handler().postDelayed({
                    stopRecording()
                }, 30000) // 30 seconds
            } catch (e: IOException) {
                Toast.makeText(this@SOSbutton, "Failed to start recording", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        isRecording = false
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            stopRecording()
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 101
    }
}