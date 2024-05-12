package com.example.rosavtodorproject2.ioc

import com.example.rosavtodorproject2.data.dataSource.AdvertisementsRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.MapRemoteDataSource
import com.example.rosavtodorproject2.data.dataSource.RoadsRemoteDataSource
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.MapPointsUseCase
import com.example.rosavtodorproject2.domain.useCases.RoadsUseCase
import dagger.Provides
import javax.inject.Scope

@Scope
annotation class AppComponentScope

@dagger.Component(modules = [DataModule::class, MainViewModelModule::class, RoadsViewModelModule::class, InteractiveMapViewModelModule::class])
@AppComponentScope
interface ApplicationComponent {
    fun getMainViewModelFactory(): MainViewModelFactory
    fun getInteractiveMapViewModelFactory(): InteractiveMapViewModelFactory
    fun getRoadsViewModelFactory(): RoadsViewModelFactory
    /*  fun getConversationViewModelFactory(): ConversationViewModelFactory*/
}

@dagger.Module
object DataModule {
    @Provides
    @AppComponentScope
    fun getAdvertisementsDataSource() = AdvertisementsRemoteDataSource()
    @Provides
    @AppComponentScope
    fun getMapDataSource() = MapRemoteDataSource()
    @Provides
    @AppComponentScope
    fun getRoadsDataSource() = RoadsRemoteDataSource()
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
object RoadsViewModelModule{
    @Provides
    @AppComponentScope
    fun getRoadsViewModelFactory(
        roadsUseCase: RoadsUseCase,
    ): RoadsViewModelFactory {
        return RoadsViewModelFactory(roadsUseCase)
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
/*@dagger.Module
object ConversationViewModelModule {
    @Provides
    @AppComponentScope
    fun getConversationViewModelFactory(
        advertisementsUseCase: AdvertisementsUseCase,
        usersUseCase: UsersUseCase,
        messageWithUserSenderUseCase: MessageWithUserSenderUseCase
    ): ConversationViewModelFactory {
        return ConversationViewModelFactory(advertisementsUseCase, usersUseCase,messageWithUserSenderUseCase)
    }
}*/
