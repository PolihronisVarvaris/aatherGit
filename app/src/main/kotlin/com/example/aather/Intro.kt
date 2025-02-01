package com.example.aather

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aather.ui.LoginActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Intro : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var currentUserId: String? = null
    private var username: String? = null

    private lateinit var imageView: ImageView
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var sosContactEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var hearingButton: Button
    private lateinit var volunteerButton: Button
    private lateinit var eyeButton: Button
    private lateinit var imageChoices: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        database = FirebaseDatabase.getInstance().reference

        initializeViews()
        loadAnimation()
    }

    private fun initializeViews() {
        imageView = findViewById(R.id.IntroLogo)
        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        heightEditText = findViewById(R.id.height)
        sosContactEditText = findViewById(R.id.sosContact)
        submitButton = findViewById(R.id.sumbitButton)
        hearingButton = findViewById(R.id.hearingbutton)
        volunteerButton = findViewById(R.id.volunteerbutton)
        eyeButton = findViewById(R.id.eyebutton)
        imageChoices = findViewById(R.id.imagechoices)
    }

    private fun loadAnimation() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        imageView.startAnimation(fadeIn)

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                imageView.visibility = View.VISIBLE
                showButtons()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun showButtons() {
        signUpButton.visibility = View.VISIBLE
        loginButton.visibility = View.VISIBLE

        val fadeInButtons = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        signUpButton.startAnimation(fadeInButtons)
        loginButton.startAnimation(fadeInButtons)

        signUpButton.setOnClickListener { onSignUpClick() }
        loginButton.setOnClickListener { onLoginClick() }
    }

    private fun onSignUpClick() {
        fadeOutButtons(signUpButton, loginButton) {
            moveImageToTop()
        }
    }

    private fun onLoginClick() {
        fadeOutButtons(signUpButton, loginButton) {
            showLoginFields()
        }
    }

    private fun showLoginFields() {
        usernameEditText.visibility = View.VISIBLE
        passwordEditText.visibility = View.VISIBLE
        submitButton.visibility = View.VISIBLE

        val fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        usernameEditText.startAnimation(fadeIn)
        passwordEditText.startAnimation(fadeIn)
        submitButton.startAnimation(fadeIn)

        submitButton.text = "Login"

        submitButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            } else {
                checkUserCredentials(username, password)
            }
        }
    }

    private fun checkUserCredentials(username: String, password: String) {
        database.child("users").orderByChild("username").equalTo(username).get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val userPassword = childSnapshot.child("password").getValue(String::class.java)
                        val usage = childSnapshot.child("usage").getValue(String::class.java)

                        if (userPassword == password) {
                            currentUserId = childSnapshot.key
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            navigateToNextScreen(usage ?: "eyes")
                            return@addOnSuccessListener
                        }
                    }
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fadeOutButtons(button1: Button, button2: Button, onComplete: () -> Unit) {
        val fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        button1.startAnimation(fadeOut)
        button2.startAnimation(fadeOut)

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                button1.visibility = View.GONE
                button2.visibility = View.GONE
                onComplete()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun moveImageToTop() {
        val moveToTop = AnimationUtils.loadAnimation(applicationContext, R.anim.move_to_top)
        imageView.startAnimation(moveToTop)

        moveToTop.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                showEditTexts(usernameEditText, passwordEditText, heightEditText, sosContactEditText)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun showEditTexts(vararg editTexts: EditText) {
        for (editText in editTexts) {
            editText.visibility = View.VISIBLE
            editText.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
        }
        submitButton.visibility = View.VISIBLE
        submitButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))

        submitButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val height = heightEditText.text.toString()
            val sosContact = sosContactEditText.text.toString()

            if (username.isEmpty() || password.isEmpty() || height.isEmpty() || sosContact.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(username, password, height, sosContact)
            }
        }
    }

    private fun registerUser(username: String, password: String, height: String, sosContact: String) {
        val user = mapOf(
            "username" to username,
            "password" to password,
            "height" to height,
            "sosContact" to sosContact
        )

        val newUserRef = database.child("users").push()
        newUserRef.setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUserId = newUserRef.key
                    retrieveUserName(currentUserId!!) { fetchedUsername ->
                        if (fetchedUsername != null) {
                            this.username = fetchedUsername
                            Toast.makeText(this, "Username retrieved: $fetchedUsername", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to retrieve username", Toast.LENGTH_SHORT).show()
                        }
                    }
                    fadeOutEditTextsAndSubmitButton {
                        fadeInImageChoices()
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fadeOutEditTextsAndSubmitButton(onComplete: () -> Unit) {
        val fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)

        usernameEditText.startAnimation(fadeOut)
        passwordEditText.startAnimation(fadeOut)
        heightEditText.startAnimation(fadeOut)
        sosContactEditText.startAnimation(fadeOut)
        submitButton.startAnimation(fadeOut)

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                usernameEditText.visibility = View.GONE
                passwordEditText.visibility = View.GONE
                heightEditText.visibility = View.GONE
                sosContactEditText.visibility = View.GONE
                submitButton.visibility = View.GONE
                onComplete()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun fadeInImageChoices() {
        imageChoices.visibility = View.VISIBLE
        val fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        imageChoices.startAnimation(fadeIn)

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                showUsageButtons()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun showUsageButtons() {
        hearingButton.visibility = View.VISIBLE
        volunteerButton.visibility = View.VISIBLE
        eyeButton.visibility = View.VISIBLE

        hearingButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
        volunteerButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
        eyeButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))

        hearingButton.setOnClickListener { currentUserId?.let { updateUserUsage(it, "ears") } }
        volunteerButton.setOnClickListener { currentUserId?.let { updateUserUsage(it, "eyes") } }
        eyeButton.setOnClickListener { currentUserId?.let { updateUserUsage(it, "volunteer") } }
    }

    private fun updateUserUsage(userId: String, usage: String) {
        val usageUpdate = mapOf("usage" to usage)
        database.child("users").child(userId).updateChildren(usageUpdate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usage updated to $usage", Toast.LENGTH_SHORT).show()
                    navigateToNextScreen(usage)
                } else {
                    Toast.makeText(this, "Update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToNextScreen(usage: String) {
        val usernameforparametre = usernameEditText.text.toString()
        val intent = when (usage) {
            "volunteer" -> Intent(this, VolunteerChoice::class.java)
            "eyes" -> Intent(this, EyesChoice::class.java)
            "ears" -> Intent(this, EarsChoice::class.java)
            else -> Intent(this, EyesChoice::class.java)
        }

        intent.putExtra("USERNAME", usernameforparametre)

        startActivity(intent)
    }

    private fun retrieveUserName(userId: String, onUserNameFetched: (String?) -> Unit) {
        database.child("users").child(userId).child("username").get().addOnSuccessListener { snapshot ->
            val username = snapshot.getValue(String::class.java)
            onUserNameFetched(username)
        }.addOnFailureListener { exception ->
            Log.e("FirebaseError", "Error fetching username: ${exception.message}")
            onUserNameFetched(null)
        }
    }

}