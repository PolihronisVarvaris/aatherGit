package com.example.aather

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3

class Basemenu : AppCompatActivity() {

    private var titleList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basemenu)

        postToList()

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        viewPager2.adapter = ViewPagerAdapter(titleList, imagesList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(viewPager2)

        // Add a PageTransformer to apply a pop animation while scrolling
        viewPager2.setPageTransformer { page, position ->
            val imagePop = page.findViewById<ImageView>(R.id.imagepop)

            // Only scale on scroll, not on click
            if (position >= -1 && position <= 1) {
                val scale = Math.max(0.85f, 1 - Math.abs(position) * 0.2f)
                imagePop.scaleX = scale
                imagePop.scaleY = scale
            } else {
                imagePop.scaleX = 1f // Reset scale when not in the visible range
                imagePop.scaleY = 1f
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
                2 -> addToList("Title $i", "Description $i", R.drawable.edulisenphoto)
                3 -> addToList("Title $i", "Description $i", R.drawable.edusignphoto)
                4 -> addToList("Title $i", "Description $i", R.drawable.noisetesterphoto)
                5 -> addToList("Title $i", "Description $i", R.drawable.bemyeyes)
            }
        }
    }
}