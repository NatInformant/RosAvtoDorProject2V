package com.example.rosavtodorproject2.data.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.MapRemoteDataSource
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.ioc.AppComponentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppComponentScope
class MapPointsRepository @Inject constructor(
    val dataSource: MapRemoteDataSource
) {
    private val _points = MutableLiveData<List<MyPoint>>(emptyList())
    val points: LiveData<List<MyPoint>> = _points

    @MainThread
    suspend fun updatePoints(currentLatitude: Double, currentLongitude: Double) {
        val loadedList =
            withContext(Dispatchers.IO) { dataSource.getPoints(currentLatitude, currentLongitude) }
        _points.value = loadedList
    }
    @MainThread
    suspend fun addPoint(point: MyPoint) {

        withContext(Dispatchers.IO) {
            dataSource.addPoint(point)
        }

        dataSource.loadPoints().add(point)
        _points.value = dataSource.loadPoints()
    }
}