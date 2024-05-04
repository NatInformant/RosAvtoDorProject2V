package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.AdvertisementWithRegionName

data class AdvertisementsGetResponse (
    val allRegionsAdvertisements: List<AdvertisementWithRegionName>
)