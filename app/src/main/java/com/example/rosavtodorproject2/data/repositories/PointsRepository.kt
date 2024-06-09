package com.example.rosavtodorproject2.data.repositories

import android.net.Uri
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.MapRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.RoadPlacesRemoteDataSource
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.ioc.AppComponentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppComponentScope
class PointsRepository @Inject constructor(
    val mapPointsDataSource: MapRemoteDataSource,
    val roadPlacesDataSource: RoadPlacesRemoteDataSource
) {
    private val _points = MutableLiveData<HttpResponseState<List<MyPoint>>>(HttpResponseState.Success(emptyList()))
    val points: LiveData<HttpResponseState<List<MyPoint>>> = _points

    private val _roadPlaces =
        MutableLiveData<HttpResponseState<List<RoadPlace>>>(
            HttpResponseState.Success(
                emptyList()
            )
        )
    val roadPlaces: LiveData<HttpResponseState<List<RoadPlace>>> =
        _roadPlaces

    @MainThread
    suspend fun updatePoints(currentLatitude: Double, currentLongitude: Double) {
        val loadedList =
            withContext(Dispatchers.IO) {
                mapPointsDataSource.getPoints(currentLatitude, currentLongitude)
            }
        _points.value = loadedList
    }

    @MainThread
    suspend fun addPoint(point: MyPoint, reliability: Int, fileUris:List<Uri>) {

        withContext(Dispatchers.IO) {
            mapPointsDataSource.addPoint(point, reliability,fileUris)
        }

        mapPointsDataSource.loadPoints().value.add(point)
        _points.value = mapPointsDataSource.loadPoints()
    }

    @MainThread
    suspend fun updateOnlyRoadPlaces(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        val responseState = withContext(Dispatchers.IO) {
            roadPlacesDataSource.loadRoadPlaces(roadName, roadPlacesType, currentUserPosition)
        }
        _roadPlaces.value = responseState
    }

    @MainThread
    suspend fun updateRoadPlacesAndMapPoints(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        updatePoints(currentUserPosition.latitude, currentUserPosition.longitude)
        updateOnlyRoadPlaces(roadName, roadPlacesType, currentUserPosition)
    }
}