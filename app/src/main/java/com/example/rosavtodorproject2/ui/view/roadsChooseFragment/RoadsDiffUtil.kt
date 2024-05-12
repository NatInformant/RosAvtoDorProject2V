package com.example.rosavtodorproject2.ui.view.roadsChooseFragment

import androidx.recyclerview.widget.DiffUtil
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.Road

class RoadsDiffUtil: DiffUtil.ItemCallback<Road>() {
    override fun areItemsTheSame(oldItem: Road, newItem: Road): Boolean {
        return oldItem.roadName == newItem.roadName
    }

    override fun areContentsTheSame(oldItem: Road, newItem: Road): Boolean {
        return oldItem == newItem
    }
}