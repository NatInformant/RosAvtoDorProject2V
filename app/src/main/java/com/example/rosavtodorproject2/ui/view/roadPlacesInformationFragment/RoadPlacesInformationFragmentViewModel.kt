package com.example.rosavtodorproject2.ui.view.roadPlacesInformationFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.domain.useCases.RoadPlacesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoadPlacesInformationFragmentViewModel @Inject constructor(
    private val roadPlacesUseCase: RoadPlacesUseCase,
) : ViewModel() {
    val roadPlaces: LiveData<HttpResponseState<List<RoadPlace>>> =
        roadPlacesUseCase.roadPlaces

    fun updateOnlyRoadPlaces(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        viewModelScope.launch {
            roadPlacesUseCase.updateOnlyRoadPlaces(roadName, roadPlacesType, currentUserPosition)
        }
    }
    fun updateRoadPlacesAndMapPoints(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        viewModelScope.launch {
            roadPlacesUseCase.updateRoadPlacesAndMapPoints(roadName, roadPlacesType, currentUserPosition)
        }
    }
}