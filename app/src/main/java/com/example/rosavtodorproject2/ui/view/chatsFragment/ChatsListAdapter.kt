/*
package com.example.rosavtodorproject2.ui.view.chatsFragment

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.databinding.ChatElementBinding
import com.example.rosavtodorproject2.ui.model.ChatElementModel
import java.text.SimpleDateFormat
import java.util.Locale

class ChatsListAdapter(
    chatsDiffUtil: ChatsDiffUtil,
    private val onItemClick: (View, Int, String, Int) -> Unit,
) : ListAdapter<ChatElementModel, ChatsListAdapter.ChatElementViewHolder>(chatsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val chatElementViewBinding = ChatElementBinding.inflate(layoutInflater, parent, false)

        return ChatElementViewHolder(
            chatElementViewBinding
        )
    }

    override fun onBindViewHolder(holder: ChatElementViewHolder, position: Int) {
        val chatElement = holder.itemView
        chatElement.setOnClickListener {
            onItemClick(
                it,
                currentList[position].id,
                currentList[position].userName,
                currentList[position].userPictureResourcesId,
            )
        }
        holder.onBind(currentList[position])
    }

    class ChatElementViewHolder(
        private val itemChatBinding: ChatElementBinding
    ) : RecyclerView.ViewHolder(itemChatBinding.root) {
        fun onBind(chatElementModel: ChatElementModel) {
            itemChatBinding.userName.text = chatElementModel.userName

            itemChatBinding.userPicture.setImageBitmap(

                //Я знаю, что некрасиво, но если не так, то при плохом интернете прога упадёт.
                AppCompatResources.getDrawable(
                    itemView.context,
                    chatElementModel.userPictureResourcesId
                )?.toBitmap(dpToPx(70), dpToPx(70))
                    ?: AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.empty_person_avatar
                    )?.toBitmap(dpToPx(70), dpToPx(70))
            )
            itemChatBinding.userLastMessage.text = chatElementModel.userLastMessage

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY)
            itemChatBinding.userLastMessageDate.text =
                sdf.format(chatElementModel.userLastMessageDate)
        }

        private fun dpToPx(dp: Int): Int =
            (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}*/
