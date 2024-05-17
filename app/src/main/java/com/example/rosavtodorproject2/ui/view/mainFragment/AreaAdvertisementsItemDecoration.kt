package com.example.rosavtodorproject2.ui.view.mainFragment

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AreaAdvertisementsItemDecoration (
    private val leftOffset: Int = 0,
    private val topOffset: Int = 0,
    private val rightOffset: Int = 0,
    private val bottomOffset: Int = 0,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if(parent.getChildLayoutPosition(view)!=0){
            outRect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        }
    }
}