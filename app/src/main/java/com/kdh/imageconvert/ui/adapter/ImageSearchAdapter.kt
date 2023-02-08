package com.kdh.imageconvert.ui.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kdh.imageconvert.GlideApp
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.data.model.SearchData
import com.kdh.imageconvert.databinding.SearchImageItemBinding
import timber.log.Timber

class ImageSearchAdapter() :  ListAdapter<Document, ImageSearchAdapter.ImageSearchViewHolder>(ImageSearchDiffCallback()){

    private var listener : OnItemClickListener? = null

    interface  OnItemClickListener{
        fun onItemClicked(position: Int,data: Document) : Unit
    }

    fun setClickListener(itemListener : OnItemClickListener){
        this.listener = itemListener
    }


    inner class ImageSearchViewHolder(private val binding : SearchImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(document : Document){
            Log.d("dodo55", "ImageSearchViewHolder")
            GlideApp.with(binding.ivImageSearch).load(document.thumbnail_url).into(binding.ivImageSearch)
            binding.tvImageSize.text = "${document.width} x ${document.height}"

            binding.ivImageSearch.setOnClickListener{
                listener?.onItemClicked(bindingAdapterPosition,document)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSearchViewHolder {
        Log.d("dodo55", "onCreateViewHolder")
        val binding = SearchImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSearchViewHolder, position: Int) {
        Log.d("dodo55", "onBindViewHolder")
        holder.bind(getItem(position))
    }


}

class ImageSearchDiffCallback : DiffUtil.ItemCallback<Document>(){
    override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem.image_url == newItem.image_url
    }

    override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem == newItem
    }

}