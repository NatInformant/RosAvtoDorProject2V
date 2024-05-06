package com.example.rosavtodorproject2.ui.view.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent


    /* Пример использования каллбек функции, опять же потом пригодиться
    private var adapter: ChatsListAdapter = ChatsListAdapter(
        chatsDiffUtil = ChatsDiffUtil(),
        onItemClick = ::onRecyclerItemClick,
    )

    //Так при этом конструктор класса выглядел:
    class ChatsListAdapter(
        chatsDiffUtil: ChatsDiffUtil,
        private val onItemClick: (View, Int, String, Int) -> Unit,
    )

    */

    private var adapter: AreaAdvertisementsListAdapter = AreaAdvertisementsListAdapter(
        areaAdvertisementsDiffUtil = AreaAdvertisementsDiffUtil(),
    )

    private val viewModel: MainFragmentViewModel by viewModels { applicationComponent.getMainViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MainFragmentBinding.inflate(layoutInflater, container, false)

        setUpToolBar()
        setUpAdvertisementsList()

        updateAdvertisements()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToInteractiveMapPanel.root.setOnClickListener {
            findNavController().navigate(R.id.action_chatsFragment_to_interactiveMapFragment)
        }
    }

    private fun setUpToolBar() {
        val navController = NavHostFragment.findNavController(this)

        val sideBar = binding.navView
        sideBar.setupWithNavController(navController)

        val appBarConfiguration =
            AppBarConfiguration(navController.graph, drawerLayout = binding.drawerLayout)
        val toolbar = binding.toolBarPanel

        toolbar.setupWithNavController(navController, appBarConfiguration)

        toolbar.children.forEach {
            if (it is ImageButton) {
                val scale = resources.getString(R.string.toolbar_icons_scale).toFloat()
                it.scaleX = scale
                it.scaleY = scale
            }
        }
    }

    private fun setUpAdvertisementsList() {
        val allAreasAdvertisementsRecyclerView: RecyclerView =
            binding.allAreasAdvertisementsRecyclerList

        allAreasAdvertisementsRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        allAreasAdvertisementsRecyclerView.layoutManager = layoutManager

        viewModel.advertisements.observe(viewLifecycleOwner) { newAdvertisements ->
            adapter.submitList(newAdvertisements)
        }
        allAreasAdvertisementsRecyclerView.addItemDecoration(
            AreaAdvertisementsItemDecoration(topOffset = 10)
        )

        binding.swipeRefreshLayoutForAdvertisementsList.setOnRefreshListener {
            updateAdvertisements()
            binding.swipeRefreshLayoutForAdvertisementsList.isRefreshing = false
        }
    }

    private fun updateAdvertisements() {
        viewModel.updateAdvertisements(
            Toast.makeText(
                requireContext(),
                "Без доступа к интернету приложение не сможет работать",
                Toast.LENGTH_LONG
            )
        )
    }
    //Пример call-back функции, потом пригодиться
    /*fun onRecyclerItemClick(recyclerItemView:View, collocutorId:Int,collocutorName:String,collocutorPictureResourceId:Int){

        val action = ChatsFragmentDirections.actionChatsFragmentToConversationFragment(
            collocutorId = collocutorId,
            collocutorName = collocutorName,
            collocutorPictureResourceId = collocutorPictureResourceId,
        )

        Navigation.findNavController(recyclerItemView).navigate(action)
    }*/
}
