package com.example.rosavtodorproject2.ui.view.roadInformationFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadAdvertisementsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoadInformationFragmentViewModel @Inject constructor(
    private val roadAdvertisementsUseCase: RoadAdvertisementsUseCase,
) : ViewModel() {
    private val _roadAdvertisements =
        MutableLiveData<HttpResponseState<List<Advertisement>>>(
            HttpResponseState.Loading
        )
    val roadAdvertisements: LiveData<HttpResponseState<List<Advertisement>>> =
        _roadAdvertisements

    fun updateRoadAdvertisements(roadName: String) {
        viewModelScope.launch {
            _roadAdvertisements.postValue(
                roadAdvertisementsUseCase.updateRoadAdvertisements(
                    roadName
                )
            )
        }
    }
}