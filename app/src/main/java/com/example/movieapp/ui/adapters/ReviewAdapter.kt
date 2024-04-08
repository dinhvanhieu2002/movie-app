package com.example.movieapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.api.models.ReviewX
import com.example.movieapp.utils.ApiHelper

class ReviewAdapter(val context: Context) : RecyclerView.Adapter<ReviewAdapter.ReviewUserViewHolder>() {
    inner class ReviewUserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHero : ImageView = view.findViewById(R.id.ivHero)
        val tvTitle : TextView = view.findViewById(R.id.tvTitle)
        val tvTime : TextView = view.findViewById(R.id.tvTime)
        val tvReview : TextView = view.findViewById(R.id.tvReview)
    }

    private val differCallback = object : DiffUtil.ItemCallback<ReviewX>() {
        override fun areItemsTheSame(oldItem: ReviewX, newItem: ReviewX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReviewX, newItem: ReviewX): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewUserViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_review_user_item, parent, false)
        return ReviewUserViewHolder(view)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ReviewUserViewHolder, position: Int) {
        val item = differ.currentList[position]

        Glide.with(context)
            .load(ApiHelper.getPosterPath((item.mediaPoster)))
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(holder.ivHero)

        holder.tvTitle.text = item.mediaTitle
        holder.tvTime.text = ApiHelper.convertDateTime(item.createdAt)
        holder.tvReview.text = item.content
    }
}