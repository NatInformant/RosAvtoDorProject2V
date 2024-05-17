package com.example.rosavtodorproject2.data.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.RoadPlacesRemoteDataSource
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.ioc.AppComponentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppComponentScope
class RoadPlacesRepository @Inject constructor(
    val dataSource: RoadPlacesRemoteDataSource
) {
    private val _roadPlaces =
        MutableLiveData<HttpResponseState<List<RoadPlace>>>(
            HttpResponseState.Success(
                emptyList()
            )
        )
    val roadPlaces: LiveData<HttpResponseState<List<RoadPlace>>> =
        _roadPlaces

    @MainThread
    suspend fun updateRoadPlaces(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        val responseState = withContext(Dispatchers.IO) {
            dataSource.loadRoadPlaces(roadName, roadPlacesType, currentUserPosition)
        }
        _roadPlaces.value = responseState
    }
}