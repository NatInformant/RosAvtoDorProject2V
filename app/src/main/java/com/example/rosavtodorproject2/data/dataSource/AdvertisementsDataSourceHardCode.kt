package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.AdvertisementWithRegionName
import java.util.SortedMap
import java.util.TreeMap

class AdvertisementsDataSourceHardCode {
    private val advertisementsWithRegionNames: List<AdvertisementWithRegionName> = listOf(
        AdvertisementWithRegionName(
            "Буран",
            "В области страшный буран, бегите глубцы, а то оно вас сожрёт и не подавиться",
            "Челябинская область"
        ),
        AdvertisementWithRegionName(
            "Снегопад",
            "В области страшный снегопад, на дорогах больше метра снега, я вообще ХЗ зачем вы из дома вышли",
            "Челябинская область"
        ),
        AdvertisementWithRegionName(
            "Дорог нет",
            "Все дороги были срыты под ноль, т.к. а ХЗ зачем они были вообще нужны",
            "Свердловская область"
        ),
        AdvertisementWithRegionName(
            "В Кургане чума",
            "В Кургане реально бубонная чума, БЕГИТЕ ВСЕ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",
            "Курганская область"
        ),
        AdvertisementWithRegionName(
            "В Курганской области потом",
            "В Курганской области, все дороги размыло, введено черезвычайное положение, не пытайтесь даже ехать сюда.",
            "Курганская область"
        ),
    )

    private var mapRegionNameToAdvertisements: SortedMap<String, List<Advertisement>> = TreeMap()

    fun loadAdvertisements() =
        advertisementsWithRegionNames.groupBy(keySelector = { it.regionName },
            valueTransform = { Advertisement(it.title, it.description) }).toSortedMap()
}