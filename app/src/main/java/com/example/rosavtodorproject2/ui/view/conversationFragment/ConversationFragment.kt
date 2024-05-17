/*
package com.example.rosavtodorproject2.ui.view.conversationFragment

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.databinding.FragmentConversationBinding
import java.util.Calendar
import java.util.TimeZone


class ConversationFragment : Fragment() {
    var collocutorId = 0

    private lateinit var binding: FragmentConversationBinding

    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private val viewModel: ConversationFragmentViewModel by viewModels { applicationComponent.getConversationViewModelFactory() }

    */
/*private var adapter:MessagesListAdapter? = null
*//*

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        */
/*adapter = MessagesListAdapter(MessagesDiffUtil(), viewModel.currentUser.value?.id ?: -1)*//*

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversationBinding.inflate(layoutInflater, container, false)

        val args: ConversationFragmentArgs by navArgs()
        collocutorId = args.collocutorId

        setUpMessagesList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: ConversationFragmentArgs by navArgs()

        binding.collocutorName.text = args.collocutorName

        //Я знаю, что некрасиво, но если не так, то при плохом интернете прога упадёт.
        binding.collocutorPicture.setImageBitmap(
            AppCompatResources.getDrawable(requireContext(), args.collocutorPictureResourceId)
                ?.toBitmap(dpToPx(70), dpToPx(70))
            ?:
            AppCompatResources.getDrawable(requireContext(), R.drawable.empty_person_avatar)
                ?.toBitmap(dpToPx(70), dpToPx(70))
        )

        binding.backToChatsPanelButton.setOnClickListener {
            findNavController().navigate(R.id.action_conversationFragment_to_chatsFragment)
        }

        binding.sendMessageButton.setOnClickListener {
            viewModel.sendMessage(
                userRecieverId = collocutorId,
                text = binding.messageEditText.text.toString(),
                sendDate = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+2")).time
            )
        }
    }

    private fun setUpMessagesList() {

       */
/* val messagesRecyclerView: RecyclerView = binding.messagesList

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        layoutManager.stackFromEnd = true
        messagesRecyclerView.adapter = adapter
        messagesRecyclerView.layoutManager = layoutManager


        viewModel.messages.observe(viewLifecycleOwner) { newMessages ->
            val messagesInThisConversation =
                newMessages.filter { it.userRecieverId == collocutorId || it.userSenderId == collocutorId }
            adapter?.submitList(messagesInThisConversation)
            if (messagesInThisConversation.isNotEmpty()) {
                messagesRecyclerView.smoothScrollToPosition(messagesInThisConversation.count() - 1)
            }
        }

        messagesRecyclerView.addItemDecoration(
            MessageElementOffsetItemDecoration(
                leftOffset = dpToPx(
                    resources.getInteger(R.integer.horizontal_message_offset)
                ),
                bottomOffset = dpToPx(
                    resources.getInteger(R.integer.bottom_message_offset)
                ),
                rightOffset = dpToPx(
                    resources.getInteger(R.integer.horizontal_message_offset)
                ),
            )
        )*//*

    }

    private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
}*/
