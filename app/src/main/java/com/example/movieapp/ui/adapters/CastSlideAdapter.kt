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
import com.example.movieapp.api.models.Cast
import com.example.movieapp.utils.ApiHelper

class CastSlideAdapter(val context: Context) : RecyclerView.Adapter<CastSlideAdapter.CastViewHolder>() {
    inner class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHero: ImageView = view.findViewById(R.id.ivHero)
        val tvName: TextView = view.findViewById(R.id.tvPeopleName)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_cast_item, parent, false)
        return CastViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.itemView.apply {
            if(item.profilePath != null) {
                Glide.with(context)
                    .load(ApiHelper.getPosterPath(item.profilePath))
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.ivHero)
            } else {
                holder.ivHero.setImageResource(R.drawable.ic_broken_image)
                isEnabled = false
            }


            holder.tvName.text = item.name

            setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }
        }
    }

    private var onItemClickListener: ((Cast) -> Unit)? = null

    fun setOnClickListener(listener: (Cast) -> Unit) {
        onItemClickListener = listener
    }
}