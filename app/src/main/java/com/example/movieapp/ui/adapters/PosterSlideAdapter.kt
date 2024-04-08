package com.example.movieapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.api.models.Poster
import com.example.movieapp.utils.ApiHelper

class PosterSlideAdapter(val context: Context) : RecyclerView.Adapter<PosterSlideAdapter.PosterSlideViewHolder>() {
    inner class PosterSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster : ImageView = view.findViewById(R.id.ivHero)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Poster>() {
        override fun areItemsTheSame(oldItem: Poster, newItem: Poster): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: Poster, newItem: Poster): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterSlideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_poster_item, parent, false)
        return PosterSlideViewHolder(view)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun onBindViewHolder(holder: PosterSlideAdapter.PosterSlideViewHolder, position: Int) {
        val item = differ.currentList[position]

        Glide.with(context)
            .load(ApiHelper.getPosterPath(item.filePath))
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(holder.ivPoster)

    }



}