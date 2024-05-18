package com.example.rosavtodorproject2.ui.view.roadPlacesInformationFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.databinding.RoadPlaceElementBinding

class RoadPlacesListAdapter(
    private val getRoadPlaceTitle: (String) -> String,
    private val getDistanceToRoadPlace: (Double) -> String,
    private val onRoadPlaceItemButtonClick:(Coordinates) -> Unit,
    roadPlacesDiffUtil: RoadPlacesDiffUtil,
) : ListAdapter<RoadPlace, RoadPlacesListAdapter.RoadPlaceElementViewHolder>(roadPlacesDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadPlaceElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val roadPlaceElementBinding = RoadPlaceElementBinding.inflate(layoutInflater, parent, false)

        return RoadPlaceElementViewHolder(
            getRoadPlaceTitle,
            getDistanceToRoadPlace,
            onRoadPlaceItemButtonClick,
            roadPlaceElementBinding
        )
    }

    override fun onBindViewHolder(holder: RoadPlaceElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class RoadPlaceElementViewHolder(
        private val getRoadPlaceTitle: (String) -> String,
        private val getDistanceToRoadPlace: (Double) -> String,
        private val onRoadPlaceItemButtonClick:(Coordinates) -> Unit,
        private val roadPlaceElementBinding: RoadPlaceElementBinding
    ) : RecyclerView.ViewHolder(roadPlaceElementBinding.root) {
        fun onBind(roadPlace: RoadPlace) {
            roadPlaceElementBinding.roadPlaceTitle.text = getRoadPlaceTitle(roadPlace.name)

            roadPlaceElementBinding.distanceToPlaceTextView.text =
                getDistanceToRoadPlace(roadPlace.distanceFromUser)
            roadPlaceElementBinding.roadPlaceGoToYandexMapButton.setOnClickListener {
                onRoadPlaceItemButtonClick(roadPlace.coordinates)
            }
        }
    }
}