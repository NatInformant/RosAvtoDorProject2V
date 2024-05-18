package com.example.rosavtodorproject2.ioc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rosavtodorproject2.domain.useCases.MapPointsUseCase
import com.example.rosavtodorproject2.ui.view.interactiveMapFragment.InteractiveMapFragmentViewModel
import javax.inject.Inject

class InteractiveMapViewModelFactory @Inject constructor(
    private val mapPointsUseCase: MapPointsUseCase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InteractiveMapFragmentViewModel(
            mapPointsUseCase = mapPointsUseCase,
        ) as T
    }
}