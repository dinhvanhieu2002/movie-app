package com.example.movieapp.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.api.models.Result
import com.example.movieapp.utils.ApiHelper.Companion.getBackdropPath
import com.example.movieapp.utils.Constants.Companion.getGenres
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.DecimalFormat
class HeroSlideAdapter(val mediaType: String, val context: Context) : RecyclerView.Adapter<HeroSlideAdapter.HeroSlideViewHolder>() {

    inner class HeroSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHero: ImageView = view.findViewById(R.id.ivHero)
        val tvTitle: TextView = view.findViewById(R.id.tvMediaTitle)
        val tvOverview: TextView = view.findViewById(R.id.tvMediaOverview)
        val tvCircularRate: TextView = view.findViewById(R.id.tvCircularRate)
        val genresGroup: ChipGroup = view.findViewById(R.id.genresGroup)
        val buttonWatch: Button = view.findViewById(R.id.btnWatch)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroSlideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_hero_item, parent, false)
        return HeroSlideViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: HeroSlideViewHolder, position: Int) {
        val item = differ.currentList[position]
        Glide.with(holder.ivHero.context.applicationContext)
            .load(getBackdropPath((item.backdropPath ?: item.posterPath!!)))
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(holder.ivHero)
        Log.e("image hero", getBackdropPath((item.backdropPath ?: item.posterPath!!)))
        holder.tvTitle.text = item.title ?: item.name
        holder.tvOverview.text = item.overview

        holder.tvCircularRate.text = DecimalFormat("#.#").format(item.voteAverage).toString()

        holder.genresGroup.removeAllViews()
        item.genreIds.subList(0, minOf(2, item.genreIds.size)).mapIndexed { index, i ->
            print(index)
            val chip = createChip(i)
            holder.genresGroup.addView(chip)
        }

        holder.buttonWatch.setOnClickListener {
            onItemClickListener?.let {
                it(item)
            }
        }


    }

    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }

    fun createChip(i: Int) : Chip {
        val chip = Chip(context)
        chip.text = getGenres(mediaType).find {
            it.id == i
        }?.name
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 0, 0)

        chip.gravity = Gravity.CENTER
        chip.layoutParams = layoutParams
        chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)))
        chip.minHeight = 48
        chip.isChipIconVisible = false
        chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
        Log.e("create chip","${chip.text}")
        return chip
    }


}