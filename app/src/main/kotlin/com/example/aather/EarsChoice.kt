package com.example.aather

import android.os.Bundle
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

class EarsChoice : AppCompatActivity() {

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

        val user = FirebaseAuth.getInstance().currentUser

        firebaseClient = FirebaseClient(database, Gson())

        fetchUserUsage()
    }

    private fun fetchUserUsage() {
        firebaseClient.fetchUserUsageFromDatabase(currentUserId) { userUsage ->

            postToList()
            val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
            val adapter = ViewPagerAdapter(titleList, imagesList, currentUserId,database)
            viewPager2.adapter = adapter
            viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            val indicator = findViewById<CircleIndicator3>(R.id.indicator)
            indicator.setViewPager(viewPager2)

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
                1 -> addToList("Title $i", "Description $i", R.drawable.deafbackimage)
                2 -> addToList("Title $i", "Description $i", R.drawable.edusignbackimage)
                3 -> addToList("Title $i", "Description $i", R.drawable.edurecorderbackimage)
                4 -> addToList("Title $i", "Description $i", R.drawable.noicemeterbackimage)
                5 -> addToList("Title $i", "Description $i", R.drawable.settingsbackimage)
            }
        }
    }
}