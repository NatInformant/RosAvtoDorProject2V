package com.example.rosavtodorproject2.domain.useCases

import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.dataSource.RoadsRemoteDataSource
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.Road
import com.example.rosavtodorproject2.data.repositories.AdvertisementsRepository
import com.example.rosavtodorproject2.data.repositories.RoadsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RoadsUseCase @Inject constructor(
    private val roadsRepository: RoadsRepository
) {
    val roads: LiveData<HttpResponseState<List<Road>>> = roadsRepository.roads

    suspend fun updateRoads() {
        withContext(Dispatchers.Main) {
            roadsRepository.updateRoads()
        }
    }
}