package com.example.rosavtodorproject2.ui.view.roadPlaceInformationFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.databinding.RoadAdvertisementElementBinding
import com.example.rosavtodorproject2.databinding.RoadPlaceElementBinding

class RoadPlacesListAdapter(
    roadPlacesDiffUtil: RoadPlacesDiffUtil,
) : ListAdapter<RoadPlace, RoadPlacesListAdapter.RoadPlaceElementViewHolder>(roadPlacesDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadPlaceElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val roadPlaceElementBinding = RoadPlaceElementBinding.inflate(layoutInflater, parent, false)

        return RoadPlaceElementViewHolder(
            roadPlaceElementBinding
        )
    }

    override fun onBindViewHolder(holder: RoadPlaceElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class RoadPlaceElementViewHolder(
        private val roadPlaceElementBinding: RoadPlaceElementBinding
    ) : RecyclerView.ViewHolder(roadPlaceElementBinding.root) {
        fun onBind(roadPlace: RoadPlace) {
            roadPlaceElementBinding.roadPlaceTitle.text = roadPlace.name
            roadPlaceElementBinding.distanceToPlaceTextView.text =
                roadPlace.distanceFromUser.toString()
        }
    }
}