package com.example.rosavtodorproject2.ui.view.mainFragment

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.HttpResponseState

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
    val advertisements: LiveData<HttpResponseState<List<Pair<String,List<Advertisement>>>>> =
        advertisementsUseCase.advertisements

    /*init {
        updateAdvertisements()
    }*/
    fun updateAdvertisements() {
        viewModelScope.launch {
            advertisementsUseCase.updateAdvertisements()
        }
    }
}