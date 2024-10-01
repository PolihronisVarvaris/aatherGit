package com.example.aather

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.aather.firebaseClient.FirebaseClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import me.relex.circleindicator.CircleIndicator3

class EyesChoice : AppCompatActivity() {


    private var titleList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()
    private lateinit var firebaseClient: FirebaseClient
    private lateinit var database: DatabaseReference
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basemenu)
        currentUserId=intent.getStringExtra("USERNAME")?: "default_username"

        database = FirebaseDatabase.getInstance().reference

        // Get the current user's ID from Firebase Authentication
        val user = FirebaseAuth.getInstance().currentUser

        firebaseClient = FirebaseClient(database, Gson())

        // Fetch the user usage type
        fetchUserUsage()
    }

    private fun fetchUserUsage() {
        firebaseClient.fetchUserUsageFromDatabase(currentUserId) { userUsage ->
            // Now you have the userUsage value (e.g., "volunteer", "eyes", "ears")

            // Continue with setting up the ViewPager once the usage is fetched
            postToList()
            val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
            val adapter = ViewPagerAdapter(titleList, imagesList, currentUserId,database)
            viewPager2.adapter = adapter
            viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            val indicator = findViewById<CircleIndicator3>(R.id.indicator)
            indicator.setViewPager(viewPager2)

            // Add a PageTransformer to apply a pop animation while scrolling
            viewPager2.setPageTransformer { page, position ->
                val imagePop = page.findViewById<ImageView>(R.id.imagepop)
                if (position >= -1 && position <= 1) {
                    val scale = Math.max(0.85f, 1 - Math.abs(position) * 0.2f)
                    imagePop.scaleX = scale
                    imagePop.scaleY = scale
                } else {
                    imagePop.scaleX = 1f
                    imagePop.scaleY = 1f
                }
            }
        }
    }

    private fun addToList(title: String, description: String, image: Int) {
        titleList.add(title)
        imagesList.add(image)
    }

    private fun postToList() {
        for (i in 1..5) {
            when (i) {
                1 -> addToList("Title $i", "Description $i", R.drawable.mannualphoto)
                2 -> addToList("Title $i", "Description $i", R.drawable.edusignphoto)
                3 -> addToList("Title $i", "Description $i", R.drawable.mannualphoto)
                4 -> addToList("Title $i", "Description $i", R.drawable.edusignphoto)
                5 -> addToList("Title $i", "Description $i", R.drawable.mannualphoto)
            }
        }
    }

    private fun retrieveUsage(userId: String, onUserNameFetched: (String?) -> Unit) {
        database.child("users").child(userId).child("username").get().addOnSuccessListener { snapshot ->
            val username = snapshot.getValue(String::class.java)
            onUserNameFetched(username) // Return the fetched username
        }.addOnFailureListener { exception ->
            Log.e("FirebaseError", "Error fetching username: ${exception.message}")
            onUserNameFetched(null) // Return null if there was an error
        }
    }
}