package com.example.rosavtodorproject2.domain.useCases

import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.repositories.MapPointsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MapPointsUseCase @Inject constructor(
    private val mapPointsRepository: MapPointsRepository,
) {
    val points: LiveData<List<MyPoint>> = mapPointsRepository.points
    suspend fun updatePoints(currentLatitude: Double, currentLongitude: Double) {
        withContext(Dispatchers.Main) {
            mapPointsRepository.updatePoints(currentLatitude, currentLongitude)
        }
    }

    suspend fun addPoint(point: MyPoint,reliability:Int) {
        withContext(Dispatchers.Main) {
            mapPointsRepository.addPoint(point,reliability)
        }
    }
}