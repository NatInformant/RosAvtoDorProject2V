package com.example.rosavtodorproject2.domain.useCases

import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.data.repositories.RoadPlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoadPlacesUseCase @Inject constructor(
    private val roadPlacesRepository: RoadPlacesRepository,
) {
    val roadPlaces: LiveData<HttpResponseState<List<RoadPlace>>> =
        roadPlacesRepository.roadPlaces

    suspend fun updateRoadPlaces(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        withContext(Dispatchers.Main) {
            roadPlacesRepository.updateRoadPlaces(roadName, roadPlacesType, currentUserPosition)
        }
    }
}