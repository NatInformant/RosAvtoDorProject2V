package com.example.rosavtodorproject2.data.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.RoadAdvertisementsRemoteDataSource
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.ioc.AppComponentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppComponentScope
class RoadAdvertisementsRepository @Inject constructor(
    val dataSource: RoadAdvertisementsRemoteDataSource
) {
    private val _roadAdvertisements =
        MutableLiveData<HttpResponseState<List<Advertisement>>>(
            HttpResponseState.Success(
                emptyList()
            )
        )
    val roadAdvertisements: LiveData<HttpResponseState<List<Advertisement>>> =
        _roadAdvertisements

    @MainThread
    suspend fun updateAdvertisements(roadName:String) {
        val responseState = withContext(Dispatchers.IO) {
            dataSource.loadRoadAdvertisements(roadName)
        }
        _roadAdvertisements.value = responseState
    }
}