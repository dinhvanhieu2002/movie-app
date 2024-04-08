package com.example.movieapp.ui.adapters

import android.content.Context
import android.util.Log
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

class MediaAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHero: ImageView = view.findViewById(R.id.ivHero)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val tvCircularRate: TextView = view.findViewById(R.id.tvCircularRate)
    }

    inner class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHero: ImageView = view.findViewById(R.id.ivHero)
        val tvName: TextView = view.findViewById(R.id.tvPeopleName)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            PEOPLE_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_people_item, parent, false)
                PeopleViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item, parent, false)
                MediaViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        if(holder.itemViewType == PEOPLE_TYPE && holder is PeopleViewHolder) {
            if(item.profilePath != null) {
                Glide.with(context)
                    .load(ApiHelper.getPosterPath(item.profilePath))
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.ivHero)
                holder.itemView.isEnabled = true

            } else {
                holder.ivHero.setImageResource(R.drawable.ic_broken_image)
                holder.itemView.isEnabled = false
                Log.e("Search", "click disable people")
            }

            holder.tvName.text = item.name

            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }
        } else if(holder.itemViewType == MEDIA_TYPE && holder is MediaViewHolder) {
            if(item.posterPath != null) {
                Glide.with(context)
                    .load(ApiHelper.getPosterPath((item.posterPath)))
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
            } else if(item.firstAirDate != null) {
                item.firstAirDate.split("-")[0]
            } else {
                ""
            }
            holder.tvCircularRate.text = DecimalFormat("#.#").format(item.voteAverage).toString()

            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(differ.currentList[position].profilePath != null || differ.currentList[position].knownForDepartment != null) PEOPLE_TYPE else MEDIA_TYPE
    }

    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }
    companion object {
        const val MEDIA_TYPE = 1
        const val PEOPLE_TYPE = 2
    }
}