package com.example.rosavtodorproject2.ioc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rosavtodorproject2.domain.useCases.RoadAdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadsUseCase
import com.example.rosavtodorproject2.ui.view.roadInformationFragment.RoadInformationFragmentViewModel
import com.example.rosavtodorproject2.ui.view.roadsChooseFragment.RoadsChooseFragmentViewModel
import javax.inject.Inject

class RoadInformationViewModelFactory  @Inject constructor(
    private val roadAdvertisementsUseCase: RoadAdvertisementsUseCase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoadInformationFragmentViewModel(
            roadAdvertisementsUseCase=roadAdvertisementsUseCase
        ) as T
    }
}