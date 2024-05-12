package com.example.rosavtodorproject2.ui.view.roadsChooseFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.data.models.Road
import com.example.rosavtodorproject2.databinding.AdvertisementElementBinding
import com.example.rosavtodorproject2.databinding.RoadElementBinding

class RoadsListAdapter (
    roadsDiffUtil: RoadsDiffUtil,
) : ListAdapter<Road, RoadsListAdapter.RoadElementViewHolder>(roadsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val roadElementBinding = RoadElementBinding.inflate(layoutInflater, parent, false)

        return RoadElementViewHolder(
            roadElementBinding
        )
    }

    override fun onBindViewHolder(holder: RoadElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class RoadElementViewHolder(
        private val roadElementBinding: RoadElementBinding
    ) : RecyclerView.ViewHolder(roadElementBinding.root) {
        fun onBind(road: Road) {
            roadElementBinding.roadName.text = road.roadName
        }
    }
}