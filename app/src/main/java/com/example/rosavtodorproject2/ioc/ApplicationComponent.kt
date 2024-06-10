package com.example.rosavtodorproject2.ioc

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.dataSource.AdvertisementsApi
import com.example.rosavtodorproject2.data.dataSource.AdvertisementsRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.MapPointsApi
import com.example.rosavtodorproject2.data.dataSource.MapRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.RoadAdvertisementsApi
import com.example.rosavtodorproject2.data.dataSource.RoadAdvertisementsRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.RoadPlacesApi
import com.example.rosavtodorproject2.data.dataSource.RoadPlacesRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.RoadsApi
import com.example.rosavtodorproject2.data.dataSource.RoadsRemoteDataSource
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.MapPointsUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadAdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadPlacesUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadsUseCase
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Scope

@Scope
annotation class AppComponentScope

@dagger.Component(modules = [DataModule::class, MainViewModelModule::class, RoadsViewModelModule::class, RoadsInformationViewModelModule::class, RoadPlacesInformationViewModelModule::class, InteractiveMapViewModelModule::class])
@AppComponentScope
interface ApplicationComponent {
    fun getMainViewModelFactory(): MainViewModelFactory
    fun getInteractiveMapViewModelFactory(): InteractiveMapViewModelFactory
    fun getRoadsViewModelFactory(): RoadsViewModelFactory
    fun getRoadInformationViewModelFactory(): RoadInformationViewModelFactory
    fun getRoadPlacesInformationViewModelFactory(): RoadPlacesInformationViewModelFactory
    /*  fun getConversationViewModelFactory(): ConversationViewModelFactory*/
}

@dagger.Module
object DataModule {
    @Provides
    @AppComponentScope
    fun getAdvertisementsApi(): AdvertisementsApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AdvertisementsApi::class.java)

    @Provides
    @AppComponentScope
    fun getMapPointsApi(): MapPointsApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MapPointsApi::class.java)

    @Provides
    @AppComponentScope
    fun getRoadsApi(): RoadsApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RoadsApi::class.java)

    @Provides
    @AppComponentScope
    fun getRoadAdvertisementsApi(): RoadAdvertisementsApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RoadAdvertisementsApi::class.java)

    @Provides
    @AppComponentScope
    fun getRoadPlacesApi(): RoadPlacesApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RoadPlacesApi::class.java)
}

@dagger.Module
object MainViewModelModule {
    @Provides
    @AppComponentScope
    fun getMainViewModelFactory(
        advertisementsUseCase: AdvertisementsUseCase,
    ): MainViewModelFactory {
        return MainViewModelFactory(advertisementsUseCase)
    }
}

@dagger.Module
object RoadsViewModelModule {
    @Provides
    @AppComponentScope
    fun getRoadsViewModelFactory(
        roadsUseCase: RoadsUseCase,
    ): RoadsViewModelFactory {
        return RoadsViewModelFactory(roadsUseCase)
    }
}

@dagger.Module
object RoadsInformationViewModelModule {
    @Provides
    @AppComponentScope
    fun getRoadInformationViewModelFactory(
        roadAdvertisementsUseCase: RoadAdvertisementsUseCase,
    ): RoadInformationViewModelFactory {
        return RoadInformationViewModelFactory(roadAdvertisementsUseCase)
    }
}

@dagger.Module
object RoadPlacesInformationViewModelModule {
    @Provides
    @AppComponentScope
    fun getRoadPlacesInformationViewModelFactory(
        roadPlacesUseCase: RoadPlacesUseCase,
    ): RoadPlacesInformationViewModelFactory {
        return RoadPlacesInformationViewModelFactory(roadPlacesUseCase)
    }
}

@dagger.Module
object InteractiveMapViewModelModule {
    @Provides
    @AppComponentScope
    fun getInteractiveMapViewModelFactory(
        mapPointsUseCase: MapPointsUseCase,
    ): InteractiveMapViewModelFactory {
        return InteractiveMapViewModelFactory(mapPointsUseCase)
    }
}
