package com.kdh.imageconvert.ui.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class GridSpacingItemDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager as StaggeredGridLayoutManager
        val spanCount = layoutManager.spanCount
        val position = parent.getChildAdapterPosition(view)
        if (position >= spanCount) {
            outRect.top = offset
        }
        if (position % spanCount != 0) {
            outRect.left = offset
        }
        outRect.right = offset
        outRect.bottom = offset
    }

}