package com.example.rosavtodorproject2.ui.view.roadPlacesInformationFragment

import androidx.recyclerview.widget.DiffUtil
import com.example.rosavtodorproject2.data.models.RoadPlace

class RoadPlacesDiffUtil : DiffUtil.ItemCallback<RoadPlace>() {
    override fun areItemsTheSame(oldItem: RoadPlace, newItem: RoadPlace): Boolean {
        return oldItem.coordinates == newItem.coordinates
    }

    override fun areContentsTheSame(oldItem: RoadPlace, newItem: RoadPlace): Boolean {
        return oldItem == newItem
    }
}