package com.example.rosavtodorproject2.ui.view.roadsChooseFragment

import android.content.Context
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
import com.example.rosavtodorproject2.ioc.RoadPlacesInformationViewModelFactory
import com.example.rosavtodorproject2.ioc.RoadsViewModelFactory
import com.example.rosavtodorproject2.ioc.applicationInstance
import javax.inject.Inject

class RoadsChooseFragment : Fragment() {

    private lateinit var binding: RoadsChooseFragmentBinding


    private var adapter: RoadsListAdapter = RoadsListAdapter(
        roadsDiffUtil = RoadsDiffUtil(),
        onRoadItemClick = ::onRoadItemClick,
    )

    @Inject
    lateinit var viewModelFactory: RoadsViewModelFactory
    private val viewModel: RoadsChooseFragmentViewModel by viewModels { viewModelFactory }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.applicationInstance.applicationComponent.injectRoadsChooseFragment(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = RoadsChooseFragmentBinding.inflate(layoutInflater, container, false)

        setUpRoadsList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToInteractiveMapFragmentPanel.root.setOnClickListener {
            findNavController().navigate(R.id.action_roadsChooseFragment_to_interactiveMapFragment)
        }
        binding.backToMainFragmentPanelButton.setOnClickListener {
            findNavController().navigate(R.id.action_roadsChooseFragment_to_mainFragment)
        }
    }

    private fun setUpRoadsList() {
        val roadsListRecyclerView: RecyclerView =
            binding.roadsRecyclerList

        roadsListRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        roadsListRecyclerView.layoutManager = layoutManager

        viewModel.roads.observe(viewLifecycleOwner) { httpResponseState ->
            //Для справки самому себе - в первый раз мы проваливаемся сюда
            //НЕ при изменении значения, за которым следим, а когда фрагмент инициализировался,
            //а уже дальше проваливаемся только при изменениях!

            binding.swipeRefreshLayoutForRoadsList.isRefreshing = false

            when (httpResponseState) {
                is HttpResponseState.Success -> {
                    adapter.submitList(httpResponseState.value)
                }
                is HttpResponseState.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {

                }
            }
        }

        binding.swipeRefreshLayoutForRoadsList.setOnRefreshListener {
            viewModel.updateRoads()
        }
    }

    //call-back функция, для нажатий на элемент списка
    private fun onRoadItemClick(roadName: String) {
        val action = RoadsChooseFragmentDirections.actionRoadsChooseFragmentToRoadInformationFragment(
           roadName
        )
        findNavController().navigate(action)
    }
}
