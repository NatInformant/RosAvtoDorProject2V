package com.example.rosavtodorproject2.ui.view.mainFragment

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Advertisement

import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.ui.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(
    private val advertisementsUseCase: AdvertisementsUseCase,
) : ViewModel() {
    val advertisements: LiveData<List<Pair<String, List<Advertisement>>>> =
        advertisementsUseCase.advertisements.map { it.toList() }

    /*init {
        updateAdvertisements()
    }*/
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
    fun updateAdvertisements(toast: Toast) {
        viewModelScope.launch {
            if(!checkInternetConnection()) {
                toast.show()
                return@launch
            }
            advertisementsUseCase.updateAdvertisements()
        }
    }
}