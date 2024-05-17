package com.example.rosavtodorproject2.ui.view.roadsChooseFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.data.models.Road
import com.example.rosavtodorproject2.databinding.AdvertisementElementBinding
import com.example.rosavtodorproject2.databinding.RoadElementBinding

class RoadsListAdapter (
    roadsDiffUtil: RoadsDiffUtil,
    private val onItemClick: (String) -> Unit,
) : ListAdapter<Road, RoadsListAdapter.RoadElementViewHolder>(roadsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val roadElementBinding = RoadElementBinding.inflate(layoutInflater, parent, false)

        return RoadElementViewHolder(
            roadElementBinding,
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: RoadElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class RoadElementViewHolder(
        private val roadElementBinding: RoadElementBinding,
        private val onItemClick: (String) -> Unit,
    ) : RecyclerView.ViewHolder(roadElementBinding.root) {
        fun onBind(road: Road) {
            roadElementBinding.roadElement.setOnClickListener {
                onItemClick(road.roadName)
            }
            roadElementBinding.roadName.text = road.roadName
        }
    }
}