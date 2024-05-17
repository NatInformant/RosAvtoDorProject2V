package com.example.rosavtodorproject2.ui.view.roadPlaceInformationFragment

import androidx.recyclerview.widget.DiffUtil
import com.example.rosavtodorproject2.data.models.RoadPlace

class RoadPlacesDiffUtil : DiffUtil.ItemCallback<RoadPlace>() {
    override fun areItemsTheSame(oldItem: RoadPlace, newItem: RoadPlace): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RoadPlace, newItem: RoadPlace): Boolean {
        return oldItem == newItem
    }
}