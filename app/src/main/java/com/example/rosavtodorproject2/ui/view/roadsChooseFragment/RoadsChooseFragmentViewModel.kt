package com.example.rosavtodorproject2.ui.view.roadsChooseFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.Road
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoadsChooseFragmentViewModel @Inject constructor(
    private val roadsUseCase: RoadsUseCase,
) : ViewModel() {
    private val _roads =
        MutableLiveData<HttpResponseState<List<Road>>>(
            HttpResponseState.Loading
        )

    val roads: LiveData<HttpResponseState<List<Road>>> = _roads

    init {
        updateRoads()
    }

    fun updateRoads() {
        viewModelScope.launch {
            _roads.postValue(roadsUseCase.updateRoads())
        }
    }
}