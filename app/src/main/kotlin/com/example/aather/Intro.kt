package com.example.aather

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Intro : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var sosContactEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        imageView = findViewById(R.id.IntroLogo)
        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        heightEditText = findViewById(R.id.height)
        sosContactEditText = findViewById(R.id.sosContact)

        // Load fade-in animation for the image
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
        // Make buttons visible
        signUpButton.visibility = View.VISIBLE
        loginButton.visibility = View.VISIBLE

        // Load fade-in animation for buttons
        val fadeInButtons = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Start fade-in animation for the buttons
        signUpButton.startAnimation(fadeInButtons)
        loginButton.startAnimation(fadeInButtons)

        // Set the click listeners for the buttons
        signUpButton.setOnClickListener { onSignUpClick() }
        loginButton.setOnClickListener { onLoginClick() }
    }

    private fun onSignUpClick() {
        fadeOutButtons(signUpButton, loginButton) {
            moveImageToTop() // Move the image to the top after buttons disappear
        }
    }

    private fun onLoginClick() {
        val intent = Intent(this, Basemenu::class.java)
        startActivity(intent)
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
            editText.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)) // Fade in new EditTexts
        }
    }
}