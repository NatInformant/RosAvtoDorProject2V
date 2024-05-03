package com.example.rosavtodorproject2.ui.view.chatsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.rosavtodorproject2.data.models.Advertisement

import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(
    private val advertisementsUseCase: AdvertisementsUseCase,
) : ViewModel() {
    val advertisements: LiveData<List<Pair<String, List<Advertisement>>>> =
        advertisementsUseCase.advertisements.map { it.toList() }

    init {
        updateAdvertisements()
    }

    //Я оставляю здесь отдельный метод, чтобы в будущем добавить SwipeToRefresh, к списку обьявлений
    fun updateAdvertisements() {
        advertisementsUseCase.updateAdvertisements()
    }
}