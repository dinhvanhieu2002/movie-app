package com.example.movieapp.ui.adapters

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
import com.example.movieapp.api.models.Result
import com.example.movieapp.utils.ApiHelper
import java.text.DecimalFormat

class MediaListHomeAdapter : RecyclerView.Adapter<MediaListHomeAdapter.MediaListHomeViewHolder>() {
    inner class MediaListHomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHero: ImageView = view.findViewById(R.id.ivHero)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val tvCircularRate: TextView = view.findViewById(R.id.tvCircularRate)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaListHomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_home_item, parent, false)
        return MediaListHomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MediaListHomeViewHolder, position: Int) {
        val item = differ.currentList[position]
        if(item.backdropPath != null) {
            Glide.with(holder.ivHero.context.applicationContext)
                .load(ApiHelper.getPosterPath(item.backdropPath))
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(holder.ivHero)
            holder.itemView.isEnabled = true

        } else if(item.posterPath != null) {
            Glide.with(holder.ivHero.context.applicationContext)
                .load(ApiHelper.getPosterPath(item.posterPath))
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(holder.ivHero)
            holder.itemView.isEnabled = true

        } else {
            holder.ivHero.setImageResource(R.drawable.ic_broken_image)
            holder.itemView.isEnabled = false
        }

        holder.tvTitle.text = item.title ?: item.name
        holder.tvYear.text = if(item.releaseDate != null) {
            item.releaseDate.split("-")[0]
        } else {
            item.firstAirDate!!.split("-")[0]
        }
        holder.tvCircularRate.text = DecimalFormat("#.#").format(item.voteAverage).toString()

        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(item)
            }
        }

    }

    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }
}