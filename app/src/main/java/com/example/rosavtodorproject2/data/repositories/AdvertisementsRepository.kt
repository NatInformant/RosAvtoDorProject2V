package com.example.rosavtodorproject2.data.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.AdvertisementsRemoteDataSource
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.ioc.AppComponentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppComponentScope
class AdvertisementsRepository @Inject constructor(
    val dataSource: AdvertisementsRemoteDataSource
) {
    private val _advertisements =
        MutableLiveData<HttpResponseState<List<Pair<String,List<Advertisement>>>>>(
            HttpResponseState.Success(
                emptyList()
            )
        )
    val advertisements: LiveData<HttpResponseState<List<Pair<String,List<Advertisement>>>>> =
        _advertisements

    suspend fun updateAdvertisements() {
        val responseState = withContext(Dispatchers.IO) {
            dataSource.loadAdvertisements()
        }
        _advertisements.postValue(responseState)
    }
}