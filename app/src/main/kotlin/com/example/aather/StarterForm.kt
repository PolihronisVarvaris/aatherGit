package com.example.aather

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StarterForm : AppCompatActivity(){

    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button
    private lateinit var facebookButton: Button
    private lateinit var instagramButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter_form)

        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)
        facebookButton = findViewById(R.id.facebookButton)
        instagramButton = findViewById(R.id.instagramButton)

        // Initially, hide the Facebook and Instagram buttons
        facebookButton.visibility = View.INVISIBLE
        instagramButton.visibility = View.INVISIBLE

        // Add click listener to the sign-up and log-in buttons to start the animation
        signUpButton.setOnClickListener { startFadeOutAnimation() }
        loginButton.setOnClickListener { startFadeOutAnimation() }
    }

    private fun startFadeOutAnimation() {
        // Fade out the sign-up and log-in buttons
        signUpButton.animate()
            .alpha(0f)
            .setDuration(500) // Duration for fade-out
            .withEndAction {
                signUpButton.visibility = View.GONE // Remove them from the layout after animation
                showSocialButtons() // Show the new buttons
            }

        loginButton.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                loginButton.visibility = View.GONE
                showSocialButtons()
            }
    }

    private fun showSocialButtons() {
        // Fade in the Facebook and Instagram buttons
        facebookButton.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate().alpha(1f).setDuration(500).start() // Fade-in animation
        }

        instagramButton.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate().alpha(1f).setDuration(500).start()
        }
    }
}