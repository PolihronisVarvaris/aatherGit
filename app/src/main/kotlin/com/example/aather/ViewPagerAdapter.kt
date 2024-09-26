package com.example.aather


import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView


class ViewPagerAdapter(
    private var title: List<String>,
    private var images: List<Int>
) : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val imagePop: ImageView = itemView.findViewById(R.id.imagepop)

        init {
            imagePop.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val context = itemView.context
                val intent = when (position) {
                    0 -> Intent(context, Basemenu::class.java)
                    1 -> Intent(context, EduRecorder::class.java)
                    2 -> Intent(context, StarterForm::class.java)
                    3 -> Intent(context, NoiseTester::class.java)
                    4 -> Intent(context, AiTextImage::class.java)
                    else -> Intent(context, Basemenu::class.java)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.Pager2ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.imagePop.setImageResource(images[position])
    }
}