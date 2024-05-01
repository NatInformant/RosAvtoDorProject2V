package com.example.rosavtodorproject2.ioc

import com.example.rosavtodorproject2.data.dataSource.AdvertisementsDataSourceHardCode
import com.example.rosavtodorproject2.data.dataSource.MapRemoteDataSource
import com.example.rosavtodorproject2.domain.useCases.MapPointsUseCase
import com.example.rosavtodorproject2.domain.useCases.MessageWithUserSenderUseCase
import com.example.rosavtodorproject2.domain.useCases.MessagesUseCase
import com.example.rosavtodorproject2.domain.useCases.UserWithLastMessageUseCase
import com.example.rosavtodorproject2.domain.useCases.UsersUseCase
import dagger.Provides
import javax.inject.Scope

@Scope
annotation class AppComponentScope

@dagger.Component(modules = [DataModule::class, ChatsViewModelModule::class, ConversationViewModelModule::class, InteractiveMapViewModelModule::class])
@AppComponentScope
interface ApplicationComponent {
    fun getUserWithLastMessageUseCase(): UserWithLastMessageUseCase
    fun getMessageWithUserSenderUseCase(): MessageWithUserSenderUseCase

    fun getChatsViewModelFactory(): ChatsViewModelFactory
    fun getConversationViewModelFactory(): ConversationViewModelFactory
    fun getInteractiveMapViewModelFactory(): InteractiveMapViewModelFactory
}

@dagger.Module
object DataModule {
    @Provides
    @AppComponentScope
    fun getChatsDataSource() = AdvertisementsDataSourceHardCode()
    @Provides
    @AppComponentScope
    fun getMapDataSource() = MapRemoteDataSource()
}

@dagger.Module
object ChatsViewModelModule {
    @Provides
    @AppComponentScope
    fun getChatsViewModelFactory(
        usersUseCase: UsersUseCase,
        userWithLastMessageUseCase: UserWithLastMessageUseCase
    ): ChatsViewModelFactory {
        return ChatsViewModelFactory(usersUseCase,userWithLastMessageUseCase)
    }
}

@dagger.Module
object ConversationViewModelModule {
    @Provides
    @AppComponentScope
    fun getConversationViewModelFactory(
        messagesUseCase: MessagesUseCase,
        usersUseCase: UsersUseCase,
        messageWithUserSenderUseCase: MessageWithUserSenderUseCase
    ): ConversationViewModelFactory {
        return ConversationViewModelFactory(messagesUseCase, usersUseCase,messageWithUserSenderUseCase)
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
