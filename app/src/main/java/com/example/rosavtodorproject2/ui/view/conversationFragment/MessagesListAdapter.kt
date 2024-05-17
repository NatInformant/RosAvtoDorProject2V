/*
package com.example.rosavtodorproject2.ui.view.conversationFragment

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.databinding.MessageElementBinding
import com.example.rosavtodorproject2.ui.model.MessageElementModel
import java.text.SimpleDateFormat
import java.util.Locale

class MessagesListAdapter(
    messagesDiffUtil: MessagesDiffUtil,
    private val currentUserId:Int,
) : ListAdapter<MessageElementModel, MessagesListAdapter.MessageElementViewHolder>(messagesDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageElementViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val messageElementViewBinding = MessageElementBinding.inflate(layoutInflater, parent, false)

        return MessageElementViewHolder(messageElementViewBinding,currentUserId)
    }

    override fun onBindViewHolder(holder: MessageElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class MessageElementViewHolder(
        private val itemMessageBinding: MessageElementBinding,
        private val currentUserId: Int,
    ) : RecyclerView.ViewHolder(itemMessageBinding.root) {

        fun onBind(message: MessageElementModel) {

            itemMessageBinding.messageCollocutorName.text = message.userSenderName
            itemMessageBinding.messageText.text = message.text
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY)
            itemMessageBinding.messageSendDate.text = sdf.format(message.sendDate)

            //Вот эту логику надо будет отсюда убрать, когда будем работать с полноценным получением
            // сообщений с бека, по данному собеседнику, там нужно будет в модельку сообщения
            // добавить поле isRightSide, и уже по его значению ставить Gravity.End

            if (message.userSenderId == currentUserId) {
                itemMessageBinding.messageLayout.layoutParams = LinearLayout.LayoutParams(
                    itemMessageBinding.messageLayout.layoutParams
                ).apply {
                    gravity = Gravity.END
                }
            }
        }
    }
}*/
