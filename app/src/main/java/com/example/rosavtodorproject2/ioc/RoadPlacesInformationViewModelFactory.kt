package com.example.rosavtodorproject2.ioc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rosavtodorproject2.domain.useCases.RoadPlacesUseCase
import com.example.rosavtodorproject2.ui.view.roadPlaceInformationFragment.RoadPlacesInformationFragmentViewModel
import javax.inject.Inject

class RoadPlacesInformationViewModelFactory @Inject constructor(
    private val roadPlacesUseCase: RoadPlacesUseCase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoadPlacesInformationFragmentViewModel(
            roadPlacesUseCase = roadPlacesUseCase
        ) as T
    }
}