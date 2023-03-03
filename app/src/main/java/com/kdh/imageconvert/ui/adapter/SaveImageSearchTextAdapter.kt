package com.kdh.imageconvert.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kdh.imageconvert.databinding.ItemSearchImageTextBinding
import com.kdh.imageconvert.ui.`interface`.OnItemClickListener

class SaveImageSearchTextAdapter : ListAdapter<String, SaveImageSearchTextAdapter.SearchTextViewHolder>(SearchTextDiffUtil()) {

    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTextViewHolder {
        Log.d("dodo55 ", "SaveImageSearchTextAdapter onCreateViewHolder")
        val binding = ItemSearchImageTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchTextViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchTextViewHolder, position: Int) {
        Log.d("dodo55 ", "SaveImageSearchTextAdapter onBindViewHolder")
        holder.bind(getItem(position), position)
    }

    inner class SearchTextViewHolder(private val binding: ItemSearchImageTextBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvSaveSearchText.setOnClickListener {
                // 해당 텍스트 위로 올려서 검색..

                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onItemClicked(absoluteAdapterPosition)
                }
            }
        }

        fun bind(text: String, pos: Int) {
            Log.d("dodo55 ", "SaveImageSearchTextAdapter bind")
            binding.tvSaveSearchText.text = text
        }
    }

    fun setClickListener(itemListener: OnItemClickListener) {
        this.listener = itemListener
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

