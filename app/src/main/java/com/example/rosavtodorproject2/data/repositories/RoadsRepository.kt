package com.example.rosavtodorproject2.data.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.RoadsRemoteDataSource
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
    suspend fun updateRoads(): HttpResponseState<List<Road>> {
        return withContext(Dispatchers.IO) {
            dataSource.loadRoads()
        }
    }
}
