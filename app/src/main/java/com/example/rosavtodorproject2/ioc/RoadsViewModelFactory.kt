package com.example.rosavtodorproject2.ioc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadsUseCase
import com.example.rosavtodorproject2.ui.view.mainFragment.MainFragmentViewModel
import com.example.rosavtodorproject2.ui.view.roadsChooseFragment.RoadsChooseFragmentViewModel
import javax.inject.Inject

class RoadsViewModelFactory @Inject constructor(
    private val roadsUseCase: RoadsUseCase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoadsChooseFragmentViewModel(
            roadsUseCase=roadsUseCase
        ) as T
    }
}