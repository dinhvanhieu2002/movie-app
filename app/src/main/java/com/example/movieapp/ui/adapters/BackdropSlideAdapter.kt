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
import com.example.movieapp.api.models.Backdrop
import com.example.movieapp.utils.ApiHelper

class BackdropSlideAdapter(val context: Context) : RecyclerView.Adapter<BackdropSlideAdapter.BackdropSlideViewHolder>() {
    inner class BackdropSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivBackdrop : ImageView = view.findViewById(R.id.ivBackdrop)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Backdrop>() {
        override fun areItemsTheSame(oldItem: Backdrop, newItem: Backdrop): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: Backdrop, newItem: Backdrop): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackdropSlideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_backdrop_item, parent, false)
        return BackdropSlideViewHolder(view)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun onBindViewHolder(holder: BackdropSlideAdapter.BackdropSlideViewHolder, position: Int) {
        val item = differ.currentList[position]

        Glide.with(context)
            .load(ApiHelper.getPosterPath(item.filePath))
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(holder.ivBackdrop)

    }



}