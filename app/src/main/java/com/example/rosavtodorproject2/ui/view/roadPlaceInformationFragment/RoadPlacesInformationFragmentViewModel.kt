package com.example.rosavtodorproject2.ui.view.roadPlaceInformationFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun updateRoadPlaces(roadName:String) {
        viewModelScope.launch {
            roadPlacesUseCase.updateRoadPlaces(roadName)
        }
    }
}