package com.example.aather
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import android.media.MediaRecorder
import android.os.Environment
import java.io.IOException
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import java.util.*
class SOSbutton {

    class SOSbutton(private var currentUserId: String) : AppCompatActivity() {

        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private val RECORD_AUDIO_REQUEST_CODE = 101
        private var mediaRecorder: MediaRecorder? = null
        private var isRecording = false
        private var sosContactNumber: String? = null

        private var username: String? = currentUserId

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sosbutton)

            val backButton = findViewById<Button>(R.id.backbutton)

            username = intent.getStringExtra("username")

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fetchSOSContactFromFirebase()

            requestLocationPermission()

            startImageViewAnimation()

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_AUDIO_REQUEST_CODE
                )
            } else {
                startRecording()
            }

            backButton.setOnClickListener {
                finish()
            }
        }

        private fun startImageViewAnimation() {
            val sosPhoto = findViewById<ImageView>(R.id.sosphoto)
            val sosPhotoSecond = findViewById<ImageView>(R.id.sosphotosecond)

            sosPhotoSecond.alpha = 0f

            val fadeOutPhoto = ObjectAnimator.ofFloat(sosPhoto, "alpha", 1f, 0f)
            fadeOutPhoto.duration = 500
            fadeOutPhoto.repeatCount = ValueAnimator.INFINITE

            val fadeInPhotoSecond = ObjectAnimator.ofFloat(sosPhotoSecond, "alpha", 0f, 1f)
            fadeInPhotoSecond.duration = 500
            fadeInPhotoSecond.repeatCount = ValueAnimator.INFINITE

            val fadeOutPhotoSecond = ObjectAnimator.ofFloat(sosPhotoSecond, "alpha", 1f, 0f)
            fadeOutPhotoSecond.duration = 500
            fadeOutPhotoSecond.repeatCount = ValueAnimator.INFINITE

            val fadeInPhoto = ObjectAnimator.ofFloat(sosPhoto, "alpha", 0f, 1f)
            fadeInPhoto.duration = 500
            fadeInPhoto.repeatCount = ValueAnimator.INFINITE

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(
                fadeOutPhoto,
                fadeInPhotoSecond,
                fadeOutPhotoSecond,
                fadeInPhoto
            )

            animatorSet.start()
        }

        private fun fetchSOSContactFromFirebase() {
            username?.let {
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("users").child(it)
                        .child("sosContact")

                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        sosContactNumber = snapshot.getValue(String::class.java)
                        if (sosContactNumber.isNullOrEmpty()) {
                            Toast.makeText(
                                this@SOSbutton,
                                "SOS contact not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@SOSbutton,
                                "SOS contact retrieved: $sosContactNumber",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateSOSContactInFirebase(sosContactNumber!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@SOSbutton,
                            "Failed to retrieve SOS contact: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } ?: run {
                Toast.makeText(this, "Username is null", Toast.LENGTH_SHORT).show()
            }
        }

        private fun updateSOSContactInFirebase(newContactNumber: String) {
            val userId =
                "user_specific_id"
            val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")

            databaseReference.child("sosContact").setValue(newContactNumber).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "SOS contact updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update SOS contact", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun requestLocationPermission() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            } else {
                getUserLocationAndSendSMS()
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    Toast.makeText(
                        this,
                        "Permission denied. Cannot record audio.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocationAndSendSMS()
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun getUserLocationAndSendSMS() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val message = "SOS from the App. Location: $latitude, $longitude"

                        if (!sosContactNumber.isNullOrEmpty()) {
                            sendSMS(sosContactNumber!!, message)
                        } else {
                            Toast.makeText(
                                this,
                                "No SOS contact available to send SMS",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: run {
                        Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        private fun sendSMS(phoneNumber: String, message: String) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(applicationContext, "SMS sent successfully", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: SecurityException) {
                Toast.makeText(
                    applicationContext,
                    "Permission denied to send SMS",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "Failed to send SMS: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
        private fun startRecording() {
            val audioFilename = "Recording_${UUID.randomUUID()}.mp3"
            val audioFilePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)?.absolutePath + "/" + audioFilename

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                try {
                    prepare()
                    start()
                    isRecording = true
                    Toast.makeText(this@SOSbutton, "Recording started", Toast.LENGTH_SHORT).show()

                    Handler().postDelayed({
                        stopRecording()
                    }, 30000) // 30 seconds
                } catch (e: IOException) {
                    Toast.makeText(this@SOSbutton, "Failed to start recording", Toast.LENGTH_SHORT)
                        .show()
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
}

