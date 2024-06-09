package com.example.rosavtodorproject2.domain.useCases

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.repositories.PointsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MapPointsUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    val points: LiveData<HttpResponseState<List<MyPoint>>> = pointsRepository.points
    suspend fun updatePoints(currentLatitude: Double, currentLongitude: Double) {
        withContext(Dispatchers.Main) {
            pointsRepository.updatePoints(currentLatitude, currentLongitude)
        }
    }

    suspend fun addPoint(point: MyPoint,reliability:Int, fileUris:List<Uri>) {
        withContext(Dispatchers.Main) {
            pointsRepository.addPoint(point,reliability,fileUris)
        }
    }
}