package com.kdh.imageconvert.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CustomImageView (context : Context, attrs : AttributeSet? = null, ) : AppCompatImageView(context,attrs) {

    companion object{
        private const val MAX_HEIGHT = 200
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //원본크기
        val originalWidth = drawable.intrinsicWidth
        val originalHeight = drawable.intrinsicHeight

        val newHeight = if(originalHeight > MAX_HEIGHT){
            MAX_HEIGHT
        }else{
            originalHeight
        }

        setMeasuredDimension(originalWidth,newHeight)
    }



}