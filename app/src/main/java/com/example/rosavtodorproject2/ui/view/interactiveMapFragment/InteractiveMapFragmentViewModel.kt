package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.domain.useCases.MapPointsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject

class InteractiveMapFragmentViewModel @Inject constructor(
    val mapPointsUseCase: MapPointsUseCase,
) : ViewModel() {
    val points: LiveData<List<MyPoint>> = mapPointsUseCase.points
    suspend fun checkInternetConnection():Boolean {
        return try {
            val ipAddress = withContext(Dispatchers.IO) {
                InetAddress.getByName("google.com")
            }
            !ipAddress.equals("")
        } catch (e: Exception) {
            false
        }
    }
    fun updatePoints(toast:Toast, currentLatitude: Double, currentLongitude: Double) {
        viewModelScope.launch {
            if(!checkInternetConnection()) {
                toast.show()
                return@launch
            }

            mapPointsUseCase.updatePoints(currentLatitude, currentLongitude)
        }
    }

    fun addPoint(toast:Toast, type: Int, latitude: Double, longitude: Double, text: String, reliability:Int) {
        viewModelScope.launch {
            if(!checkInternetConnection()) {
                toast.show()
                return@launch
            }

            mapPointsUseCase.addPoint(
                MyPoint(
                    type = type,
                    coordinates = Coordinates(latitude, longitude),
                    name = text,
                ),
                reliability = reliability
            )
        }
    }
}