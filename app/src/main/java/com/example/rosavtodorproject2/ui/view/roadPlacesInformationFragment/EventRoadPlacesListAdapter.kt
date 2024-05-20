package com.example.rosavtodorproject2.ui.view.roadPlacesInformationFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.databinding.EventRoadPlaceElementBinding

class EventRoadPlacesListAdapter(
    private val getRoadPlaceTitle: (String) -> String,
    private val getDistanceToRoadPlace: (Double) -> String,
    private val onRoadPlaceItemButtonClick: (Coordinates) -> Unit,
    roadPlacesDiffUtil: RoadPlacesDiffUtil,
) : ListAdapter<RoadPlace, EventRoadPlacesListAdapter.EventRoadPlaceElementViewHolder>(roadPlacesDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventRoadPlaceElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val eventRoadPlaceElementBinding = EventRoadPlaceElementBinding.inflate(layoutInflater, parent, false)

        return EventRoadPlaceElementViewHolder(
            getRoadPlaceTitle,
            getDistanceToRoadPlace,
            onRoadPlaceItemButtonClick,
            eventRoadPlaceElementBinding
        )
    }

    override fun onBindViewHolder(holder: EventRoadPlaceElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class EventRoadPlaceElementViewHolder(
        private val getRoadPlaceTitle: (String) -> String,
        private val getDistanceToRoadPlace: (Double) -> String,
        private val onRoadPlaceItemButtonClick: (Coordinates) -> Unit,
        private val roadPlaceElementBinding: EventRoadPlaceElementBinding
    ) : RecyclerView.ViewHolder(roadPlaceElementBinding.root) {
        fun onBind(eventRoadPlace: RoadPlace) {
            roadPlaceElementBinding.eventRoadPlaceTitle.text = getRoadPlaceTitle(eventRoadPlace.name)
            roadPlaceElementBinding.eventRoadPlaceDescription.text = eventRoadPlace.description

            roadPlaceElementBinding.distanceToPlaceTextView.text =
                getDistanceToRoadPlace(eventRoadPlace.distanceFromUser)
            roadPlaceElementBinding.roadPlaceGoToYandexMapButton.setOnClickListener {
                onRoadPlaceItemButtonClick(eventRoadPlace.coordinates)
            }
        }
    }
}
