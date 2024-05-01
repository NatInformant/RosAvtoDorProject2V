package com.example.rosavtodorproject2.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.AdvertisementsDataSourceHardCode
import com.example.rosavtodorproject2.data.models.Message
import com.example.rosavtodorproject2.ioc.AppComponentScope
import java.util.Date
import javax.inject.Inject

@AppComponentScope
class MessagesRepository @Inject constructor(
    val dataSource: AdvertisementsDataSourceHardCode
) {
    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    fun updateMessages() {
        _messages.value = dataSource.loadMessages()
    }

    fun addMessage(userSenderId: Int, userRecieverId: Int, text: String, sendDate: Date) {
        dataSource.loadMessages().add(
            Message(
                id = dataSource.loadMessages().size,
                userSenderId = userSenderId,
                userRecieverId = userRecieverId,
                text = text,
                sendDate = sendDate,
            )
        )
        updateMessages()
    }
}