/*
package com.example.rosavtodorproject2.ui.view.conversationFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.rosavtodorproject2.data.models.User
import com.example.rosavtodorproject2.domain.useCases.AdvertisementsUseCase
import com.example.rosavtodorproject2.domain.useCases.MessageWithUserSenderUseCase
import com.example.rosavtodorproject2.domain.useCases.UsersUseCase
import com.example.rosavtodorproject2.ui.model.MessageElementModel
import java.util.Date
import javax.inject.Inject

class ConversationFragmentViewModel @Inject constructor(
    val advertisementsUseCase: AdvertisementsUseCase,
    val usersUseCase: UsersUseCase,
    val messageWithUserSenderUseCase: MessageWithUserSenderUseCase,
) : ViewModel() {
    val currentUser: LiveData<User> = usersUseCase.currentUser
    val messages: LiveData<List<MessageElementModel>> =
        messageWithUserSenderUseCase.messageWithUserSender.map {
            it.map { messageWithUserSender -> messageWithUserSender.transformToItemModel() }
        }

    init {
        updateUsersAndMessages()
    }
    //Я оставляю здесь отдельный метод, чтобы в будущем добавить SwipeToRefresh, к списку сообщений
    //в беседе
    fun updateUsersAndMessages() {
        messageWithUserSenderUseCase.updateUsersAndMessages()
    }

    fun sendMessage(userRecieverId:Int, text :String, sendDate: Date) {
        advertisementsUseCase.addMessage(
            userSenderId = currentUser.value?.id ?: -1,
            userRecieverId = userRecieverId,
            text = text,
            sendDate = sendDate
        )
    }

    private fun MessageWithUserSender.transformToItemModel() = MessageElementModel(
        id = message.id,
        userSenderId = message.userSenderId,
        userSenderName = user.name,
        userRecieverId = message.userRecieverId,
        text = message.text,
        sendDate = message.sendDate,
    )
}*/
