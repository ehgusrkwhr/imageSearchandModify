package com.kdh.imageconvert.ui.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kdh.imageconvert.GlideApp
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.databinding.SearchDetailImageItemBinding
import com.kdh.imageconvert.ui.adapter.diffutil.ImageDiffCallback

class ImageDetailViewPagerAdapter : ListAdapter<Document, ImageDetailViewPagerAdapter.ImageDetailViewHolder>(ImageDiffCallback()) {

    private val imageDataList = mutableListOf<Document>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailViewHolder {
        val binding = SearchDetailImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageDetailViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    inner class ImageDetailViewHolder(private val binding : SearchDetailImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(document: Document, position : Int){
//            val imageData = imageDataList[position]

            GlideApp.with(binding.ivImageDetail)
                .load(document.image_url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.ivImageDetail)


            startAndEndPreload(position,binding)



            Log.d("dodo ","getCurrentList.size : ${currentList.size}")



        }
    }

    private fun startAndEndPreload(position : Int,binding : SearchDetailImageItemBinding){
        var endPosition : Int = 0
        var startPosition : Int = 0
        if(position <= currentList.size ){
             endPosition = if(position + 5 > currentList.size){
                currentList.size
            }else{
                position+5
            }
            currentList.subList(position,endPosition).map{it.image_url}.forEach {
                preload(binding.ivImageDetail.context,it)
            }
        }

        if(position > 0 ){
             startPosition = if(position - 5 > 0){
                position-5
            }else{
                0
            }
            currentList.subList(startPosition,position).map{it.image_url}.forEach {
                preload(binding.ivImageDetail.context,it)
            }
        }
    }

    private fun preload(context : Context, url : String){
        GlideApp.with(context)
            .load(url)
            .preload()
    }
}