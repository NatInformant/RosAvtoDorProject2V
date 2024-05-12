package com.example.rosavtodorproject2.data.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.AdvertisementsRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.RoadsRemoteDataSource
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.Road
import com.example.rosavtodorproject2.ioc.AppComponentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppComponentScope
class RoadsRepository @Inject constructor(
    val dataSource: RoadsRemoteDataSource
) {
    private val _roads =
        MutableLiveData<HttpResponseState<List<Road>>>(
            HttpResponseState.Success(
                emptyList()
            )
        )

    val roads: LiveData<HttpResponseState<List<Road>>> = _roads

    @MainThread
    suspend fun updateRoads() {
        val loadedList = withContext(Dispatchers.IO) {
            dataSource.loadRoads()
        }
        _roads.value = loadedList
    }
}
