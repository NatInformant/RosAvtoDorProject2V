/*
package com.example.rosavtodorproject2.ioc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rosavtodorproject2.domain.useCases.MessageWithUserSenderUseCase
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.UsersUseCase
import com.example.rosavtodorproject2.ui.view.conversationFragment.ConversationFragmentViewModel
import javax.inject.Inject

class ConversationViewModelFactory @Inject constructor(
    val advertisementsUseCase: AdvertisementsUseCase,
    val usersUseCase: UsersUseCase,
    val messageWithUserSenderUseCase: MessageWithUserSenderUseCase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConversationFragmentViewModel(
            advertisementsUseCase = advertisementsUseCase,
            usersUseCase= usersUseCase,
            messageWithUserSenderUseCase = messageWithUserSenderUseCase
        ) as T
    }
}*/
