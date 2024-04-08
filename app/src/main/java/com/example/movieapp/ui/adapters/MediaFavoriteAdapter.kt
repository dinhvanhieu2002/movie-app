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
import com.example.movieapp.api.models.Media
import com.example.movieapp.utils.ApiHelper
import java.text.DecimalFormat

class MediaFavoriteAdapter(val context: Context) : RecyclerView.Adapter<MediaFavoriteAdapter.MediaFavoriteViewHolder>() {
    inner class MediaFavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle : TextView = view.findViewById(R.id.tvTitle)
        val tvRate : TextView = view.findViewById(R.id.tvRate)
        val ivHero : ImageView = view.findViewById(R.id.ivHero)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_favorite_item, parent, false)
        return MediaFavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MediaFavoriteViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.tvTitle.text = item.mediaTitle
        holder.tvRate.text = DecimalFormat("#.#").format(item.mediaRate.toDouble()).toString()
        Glide.with(context)
            .load(ApiHelper.getPosterPath(item.mediaPoster))
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(holder.ivHero)

        holder.itemView.setOnClickListener {
            setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }
        }
    }

    private var onItemClickListener: ((Media) -> Unit)? = null

    fun setOnClickListener(listener: (Media) -> Unit) {
        onItemClickListener = listener
    }
}