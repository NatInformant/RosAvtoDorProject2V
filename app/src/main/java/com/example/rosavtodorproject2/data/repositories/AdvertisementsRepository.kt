package com.example.rosavtodorproject2.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.AdvertisementsDataSourceHardCode
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.ioc.AppComponentScope
import javax.inject.Inject

@AppComponentScope
class AdvertisementsRepository @Inject constructor(
    val dataSource: AdvertisementsDataSourceHardCode
) {
    private val _advertisements = MutableLiveData<Map<String, List<Advertisement>>>(emptyMap())
    val advertisements: LiveData<Map<String, List<Advertisement>>> = _advertisements

    fun updateAdvertisements() {
        _advertisements.value = dataSource.loadAdvertisements()
    }
}