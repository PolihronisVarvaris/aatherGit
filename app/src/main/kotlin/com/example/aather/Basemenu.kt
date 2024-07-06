package com.example.aather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3

class Basemenu : AppCompatActivity() {

    private var titleList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basemenu)

        postToList()

        val view_pager2 = findViewById<ViewPager2>(R.id.view_pager2)

        view_pager2.adapter = ViewPagerAdapter(titleList, descList, imagesList)
        view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(view_pager2)
    }

    private fun addToList(title: String, description: String, image: Int) {
        titleList.add(title)
        descList.add(description)
        imagesList.add(image)
    }

    private fun postToList() {
        for (i in 1..5) {
            when (i) {
                1 -> addToList("Title $i", "Description $i", R.drawable.mannualphoto)
                2 -> addToList("Title $i", "Description $i", R.drawable.edulisenphoto)
                3 -> addToList("Title $i", "Description $i", R.drawable.edusignphoto)
                4 -> addToList("Title $i", "Description $i", R.drawable.noisetesterphoto)
                5 -> addToList("Title $i", "Description $i", R.drawable.bemyeyes)
            }
        }
    }
}