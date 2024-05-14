package com.example.rosavtodorproject2.ui.view.roadInformationFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.databinding.RoadInformationFragmentBinding
import com.example.rosavtodorproject2.ui.view.mainFragment.AdvertisementsDiffUtil
import com.example.rosavtodorproject2.ui.view.mainFragment.AreaAdvertisementsDiffUtil
import com.example.rosavtodorproject2.ui.view.mainFragment.AreaAdvertisementsListAdapter

class RoadInformationFragment : Fragment() {

    private lateinit var binding: RoadInformationFragmentBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private var adapter: RoadAdvertisementsListAdapter = RoadAdvertisementsListAdapter(
        advertisementsDiffUtil = AdvertisementsDiffUtil(),
    )

    private val viewModel: RoadInformationFragmentViewModel by viewModels { applicationComponent.getRoadInformationViewModelFactory() }
    var roadName: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoadInformationFragmentBinding.inflate(layoutInflater, container, false)
        roadName = arguments?.getString("roadName")

        viewModel.updateRoadAdvertisements(roadName?:"")

        setUpRoadAdvertisementsList()

        binding.roadWarningsTitle.text = "Предупреждения по трассе: $roadName"
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToInteractiveMapFragmentPanel.root.setOnClickListener {
            findNavController().navigate(R.id.action_roadInformationFragment_to_interactiveMapFragment)
        }
        binding.backToRoadsChooseFragmentPanelButton.setOnClickListener {
            findNavController().navigate(R.id.action_roadInformationFragment_to_roadsChooseFragment)
        }
    }
    private fun setUpRoadAdvertisementsList() {
        val roadsListRecyclerView: RecyclerView =
            binding.roadsRecyclerList

        roadsListRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        roadsListRecyclerView.layoutManager = layoutManager

        viewModel.roadAdvertisements.observe(viewLifecycleOwner) { httpResponseState ->
            //Для справки самому себе - в первый раз мы проваливаемся сюда
            //НЕ при изменении значения, за которым следим, а когда фрагмент инициализировался,
            //а уже дальше проваливаемся только при изменениях!
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
                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.swipeRefreshLayoutForRoadAdvertisementsList.setOnRefreshListener {
            viewModel.updateRoadAdvertisements(roadName?:"")
            binding.swipeRefreshLayoutForRoadAdvertisementsList.isRefreshing = false
        }
    }
}