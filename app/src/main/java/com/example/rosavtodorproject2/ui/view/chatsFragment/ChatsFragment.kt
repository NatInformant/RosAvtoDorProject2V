package com.example.rosavtodorproject2.ui.view.chatsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.databinding.MainFragmentBinding

class ChatsFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private var adapter: ChatsListAdapter = ChatsListAdapter(
        chatsDiffUtil = ChatsDiffUtil(),
        onItemClick = ::onRecyclerItemClick,
    )

    private val viewModel: ChatsFragmentViewModel by viewModels { applicationComponent.getChatsViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MainFragmentBinding.inflate(layoutInflater, container, false)

        setUpToolBar()
        setUpChatsList()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToInteractiveMapPanel.root.setOnClickListener {
            findNavController().navigate(R.id.action_chatsFragment_to_interactiveMapFragment)
        }
    }


    fun setUpToolBar() {

        val navController = NavHostFragment.findNavController(this)

        val sideBar = binding.navView
        sideBar.setupWithNavController(navController)

        val profileMenuHeader = sideBar.getHeaderView(0)
        val userNameTextView = profileMenuHeader.findViewById<TextView>(R.id.current_user_name)

        viewModel.currentUser.observe(viewLifecycleOwner) {
            userNameTextView.text = it.name
        }

        val userNameProfilePicture =
            profileMenuHeader.findViewById<ImageView>(R.id.current_user_image)

        viewModel.currentUser.observe(viewLifecycleOwner) {
            userNameProfilePicture.setBackgroundResource(it.userPictureResourcesId)
        }

        sideBar.menu[0].setOnMenuItemClickListener {
            CurrentUserNameEditingDialogFragment(viewModel.currentUser.value?.name)
            { newCurrentUserName: String ->
                viewModel.setNewCurrentUserName(newCurrentUserName)
            }
                .show(parentFragmentManager, "NAME EDITING")
            true
        }

        val appBarConfiguration =
            AppBarConfiguration(navController.graph, drawerLayout = binding.drawerLayout)
        val toolbar = binding.profileAndSearchPanel

        toolbar.setupWithNavController(navController, appBarConfiguration)

        toolbar.children.forEach {
            if (it is ImageButton) {
                val scale = resources.getString(R.string.toolbar_icons_scale).toFloat()
                it.scaleX = scale
                it.scaleY = scale
            }
        }
    }

    fun setUpChatsList() {
        val chatsRecyclerView: RecyclerView = binding.chats

        chatsRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        chatsRecyclerView.layoutManager = layoutManager

        viewModel.chats.observe(viewLifecycleOwner) { newUsersOrMessages ->
            adapter.submitList(newUsersOrMessages)
        }

        chatsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )
    }
    fun onRecyclerItemClick(recyclerItemView:View, collocutorId:Int,collocutorName:String,collocutorPictureResourceId:Int){

        val action = ChatsFragmentDirections.actionChatsFragmentToConversationFragment(
            collocutorId = collocutorId,
            collocutorName = collocutorName,
            collocutorPictureResourceId = collocutorPictureResourceId,
        )

        Navigation.findNavController(recyclerItemView).navigate(action)
    }
}
