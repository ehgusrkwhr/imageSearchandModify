package com.kdh.imageconvert.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kdh.imageconvert.databinding.ItemSearchImageTextBinding

class SaveImageSearchTextAdapter : ListAdapter<String, SaveImageSearchTextAdapter.SearchTextViewHolder>(SearchTextDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTextViewHolder {
        val binding = ItemSearchImageTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchTextViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchTextViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class SearchTextViewHolder(private val binding: ItemSearchImageTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String, pos: Int) {
            binding.tvSaveSearchText.text = text
        }
    }
}

class SearchTextDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}