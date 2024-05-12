package com.example.rosavtodorproject2.ui.view.roadsChooseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.databinding.RoadsChooseFragmentBinding

class RoadsChooseFragment : Fragment() {

    private lateinit var binding: RoadsChooseFragmentBinding
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

        binding = RoadsChooseFragmentBinding.inflate(layoutInflater, container, false)

        setUpRoadsList()

        updateAdvertisements()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToInteractiveMapFragmentPanel.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_roadsChooseFragment)
        }
    }

    private fun setUpRoadsList() {
        val allAreasAdvertisementsRecyclerView: RecyclerView =
            binding.allAreasAdvertisementsRecyclerList

        allAreasAdvertisementsRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        allAreasAdvertisementsRecyclerView.layoutManager = layoutManager

        viewModel.advertisements.observe(viewLifecycleOwner) { httpResponseState ->
            when (httpResponseState){
                is HttpResponseState.Success -> {
                    adapter.submitList(httpResponseState.value)
                }
                is HttpResponseState.Failure ->{
                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        allAreasAdvertisementsRecyclerView.addItemDecoration(
            AreaAdvertisementsItemDecoration(topOffset = 10)
        )

        binding.swipeRefreshLayoutForRoadsList.setOnRefreshListener {
            viewModel
            binding.swipeRefreshLayoutForAdvertisementsList.isRefreshing = false
        }
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
