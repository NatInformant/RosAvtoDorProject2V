package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.domain.useCases.MapPointsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class InteractiveMapFragmentViewModel @Inject constructor(
    val mapPointsUseCase: MapPointsUseCase,
) : ViewModel() {
    val points: LiveData<HttpResponseState<List<MyPoint>>> = mapPointsUseCase.points
    private val _message = MutableLiveData<String>("")
    val message: LiveData<String> = _message
    fun updatePoints(currentLatitude: Double, currentLongitude: Double) {
        viewModelScope.launch {
            mapPointsUseCase.updatePoints(currentLatitude, currentLongitude)
        }
    }

    fun addPoint(
        type: Int,
        latitude: Double,
        longitude: Double,
        text: String,
        reliability: Int,
        filePaths: List<String>,
        roadName: String?
    ) {
        viewModelScope.launch {
            val sendingPointMessage = mapPointsUseCase.addPoint(
                MyPoint(
                    type = type,
                    coordinates = Coordinates(latitude, longitude),
                    name = text,
                    //Xз нормально ли, но пока так.
                    description = ""
                ),
                reliability = reliability,
                filePaths = filePaths,
                roadName = roadName
            )
            _message.postValue(sendingPointMessage)
        }
    }
}