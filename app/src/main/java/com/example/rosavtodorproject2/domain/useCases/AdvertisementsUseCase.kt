package com.example.rosavtodorproject2.domain.useCases

import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.repositories.AdvertisementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class AdvertisementsUseCase @Inject constructor(
    private val advertisementsRepository: AdvertisementsRepository,
) {

    suspend fun updateAdvertisements(): HttpResponseState<List<Pair<String, List<Advertisement>>>> {
        return withContext(Dispatchers.IO) {
            advertisementsRepository.updateAdvertisements()
        }
    }
}