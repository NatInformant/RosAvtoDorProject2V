package com.example.rosavtodorproject2.domain.useCases

import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.repositories.RoadAdvertisementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoadAdvertisementsUseCase @Inject constructor(
    private val roadAdvertisementsRepository: RoadAdvertisementsRepository,
) {

    suspend fun updateRoadAdvertisements(roadName:String): HttpResponseState<List<Advertisement>> {
        return withContext(Dispatchers.IO) {
            roadAdvertisementsRepository.updateAdvertisements(roadName)
        }
    }
}