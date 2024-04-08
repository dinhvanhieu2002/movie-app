package com.example.movieapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.api.models.Review
import com.example.movieapp.utils.ApiHelper

class ReviewMediaAdapter : RecyclerView.Adapter<ReviewMediaAdapter.ReviewMediaViewHolder>() {
    inner class ReviewMediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvDisplayName : TextView = view.findViewById(R.id.tvDisplayName)
        val tvTime : TextView = view.findViewById(R.id.tvTime)
        val tvReview : TextView = view.findViewById(R.id.tvReview)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewMediaViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_review_item, parent, false)
        return ReviewMediaViewHolder(view)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ReviewMediaViewHolder, position: Int) {
        val item = differ.currentList[position]


        holder.tvDisplayName.text = item.user.displayName
        holder.tvTime.text = ApiHelper.convertDateTime(item.createdAt)
        holder.tvReview.text = item.content

    }
}