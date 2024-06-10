package com.example.rosavtodorproject2.domain.useCases

import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.data.repositories.PointsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoadPlacesUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    val roadPlaces: LiveData<HttpResponseState<List<RoadPlace>>> =
        pointsRepository.roadPlaces

    suspend fun updateOnlyRoadPlaces(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        withContext(Dispatchers.IO) {
            pointsRepository.updateOnlyRoadPlaces(roadName, roadPlacesType, currentUserPosition)
        }
    }
    suspend fun updateRoadPlacesAndMapPoints(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        withContext(Dispatchers.IO) {
            pointsRepository.updateRoadPlacesAndMapPoints(roadName, roadPlacesType, currentUserPosition)
        }
    }
}