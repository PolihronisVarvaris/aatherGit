package com.example.aather

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference

class ViewPagerAdapter(
    private var title: List<String>,
    private var images: List<Int>,
    private var currentUserId: String,
    private var database: DatabaseReference
) : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    var lol = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.imagePop.setImageResource(images[position])

        // Fetch user details when binding the view holder
        Toast.makeText(holder.itemView.context, "currentUserId = $currentUserId", Toast.LENGTH_SHORT).show()
        holder.retrieveUserDetailsByUsername(currentUserId) { userDetails ->
            if (userDetails != null) {
                val userUsage = userDetails["usage"] as? String ?: "default"
                Log.d("ViewPagerAdapter", "Fetched usage: $userUsage")

                // Update 'lol' based on 'userUsage'
                when (userUsage) {
                    "eyes" -> {
                        lol = 1
                        Toast.makeText(holder.itemView.context, "usage = eyes", Toast.LENGTH_SHORT).show()
                    }
                    "volunteer" -> {
                        lol = 2
                        Toast.makeText(holder.itemView.context, "usage = volunteer", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        lol = 3
                        Toast.makeText(holder.itemView.context, "usage = ears", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val imagePop: ImageView = itemView.findViewById(R.id.imagepop)

        init {
            imagePop.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val context = itemView.context

                // Choose the right intent based on the lol value
                val intent: Intent = when (lol) {
                    1 -> handleEyesClick(position, context)
                    2 -> handleVolunteerClick(position, context)
                    3 -> handleEarsClick(position, context)
                    else -> handleEarsClick(position, context) // Default case for any unexpected value of lol
                }

                // Start the chosen activity
                context.startActivity(intent)
            }
        }

        // Add the retrieveUserDetailsByUsername method here
        fun retrieveUserDetailsByUsername(username: String, callback: (Map<String, Any>?) -> Unit) {
            database.child("users").orderByChild("username").equalTo(username).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val userDetails = snapshot.children.first().value as? Map<String, Any>
                        Log.d("ViewPagerAdapter", "User details fetched: $userDetails")
                        callback(userDetails)
                    } else {
                        Log.e("ViewPagerAdapter", "User not found with username: $username")
                        callback(null) // No user found
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ViewPagerAdapter", "Error fetching user details: ${exception.message}")
                    callback(null) // Handle the failure case
                }
        }
    }

    private fun handleVolunteerClick(position: Int, context: Context): Intent {
        return when (position) {
            0 -> Intent(context, speechtexttry::class.java)
            1 -> Intent(context, PickColorFilter::class.java)
            2 -> Intent(context, AiTextImage::class.java)
            3 -> Intent(context, AiTextImage::class.java)
            4 -> Intent(context, AiTextImage::class.java)
            else -> Intent(context, EarsChoice::class.java)
        }
    }

    private fun handleEyesClick(position: Int, context: Context): Intent {
        return when (position) {
            0 -> Intent(context, speechtexttry::class.java)
            1 -> Intent(context, Size_image::class.java)
            2 -> Intent(context, PickColorFilter::class.java)
            3 -> Intent(context, AiTextImage::class.java)
            4 -> Intent(context, AiTextImage::class.java)
            else -> Intent(context, EarsChoice::class.java)
        }
    }

    private fun handleEarsClick(position: Int, context: Context): Intent {
        return when (position) {
            0 -> Intent(context, speechtexttry::class.java)
            1 -> Intent(context, signEduAi::class.java)
            2 -> Intent(context, EduRecorder::class.java)
            3 -> Intent(context, NoiseTester::class.java)
            4 -> Intent(context, AiTextImage::class.java)
            else -> Intent(context, EarsChoice::class.java)
        }
    }
}
