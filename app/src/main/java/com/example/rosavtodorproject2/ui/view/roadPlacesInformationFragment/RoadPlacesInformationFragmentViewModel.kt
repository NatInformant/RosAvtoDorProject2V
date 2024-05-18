package com.example.rosavtodorproject2.ui.view.roadPlacesInformationFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.RoadPlace
import com.example.rosavtodorproject2.domain.useCases.RoadPlacesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoadPlacesInformationFragmentViewModel @Inject constructor(
    private val roadPlacesUseCase: RoadPlacesUseCase,
) : ViewModel() {
    val roadPlaces: LiveData<HttpResponseState<List<RoadPlace>>> =
        roadPlacesUseCase.roadPlaces

    fun updateRoadPlaces(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ) {
        viewModelScope.launch {
            roadPlacesUseCase.updateRoadPlaces(roadName, roadPlacesType, currentUserPosition)
        }
    }
}