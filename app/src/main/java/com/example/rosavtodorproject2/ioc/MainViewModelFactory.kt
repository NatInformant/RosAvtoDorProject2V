package com.example.rosavtodorproject2.ioc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.ui.view.mainFragment.MainFragmentViewModel
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(
    private val advertisementsUseCase: AdvertisementsUseCase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainFragmentViewModel(
            advertisementsUseCase=advertisementsUseCase
        ) as T
    }
}