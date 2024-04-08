package com.example.movieapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.api.models.ChatMessage

class GroupMessageAdapter : RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder>() {
    inner class GroupMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDisplayName: TextView = view.findViewById(R.id.tvDisplayName)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    private val differCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.senderId == newItem.senderId
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupMessageAdapter.GroupMessageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_message_item, parent, false)
        return GroupMessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: GroupMessageViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.tvDisplayName.text = item.senderName
        holder.tvMessage.text = item.message
    }

}