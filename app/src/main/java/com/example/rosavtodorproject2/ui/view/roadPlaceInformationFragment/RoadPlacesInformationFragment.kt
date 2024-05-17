package com.example.rosavtodorproject2.ui.view.roadPlaceInformationFragment

import android.content.res.Resources
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
import com.example.rosavtodorproject2.databinding.RoadPlacesInformationFragmentBinding

class RoadPlacesInformationFragment : Fragment() {

    private lateinit var binding: RoadPlacesInformationFragmentBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private var adapter: RoadPlacesListAdapter = RoadPlacesListAdapter(
        roadPlacesDiffUtil = RoadPlacesDiffUtil(),
    )

    private val viewModel: RoadPlacesInformationFragmentViewModel by viewModels { applicationComponent.getRoadPlacesInformationViewModelFactory() }
    private var roadName: String? = null
    private var roadPlaceType: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoadPlacesInformationFragmentBinding.inflate(layoutInflater, container, false)
        roadName = arguments?.getString("roadName")
        roadPlaceType = arguments?.getString("roadPlaceType")

        //viewModel.updateRoadPlaces(roadName ?: "")

        setUpRoadPlacesList()

        binding.roadPlacesListTitle.text = "Предупреждения по трассе: $roadName"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToRoadInformationFragmentPanelButton.setOnClickListener {
            findNavController().navigate(R.id.action_roadInformationFragment_to_roadsChooseFragment)
        }

    }

    private fun setUpRoadPlacesList() {
        val roadsListRecyclerView: RecyclerView =
            binding.roadPlacesRecyclerList

        roadsListRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        roadsListRecyclerView.layoutManager = layoutManager

        viewModel.roadPlaces.observe(viewLifecycleOwner) { httpResponseState ->
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

        binding.swipeRefreshLayoutForRoadPlacesList.setOnRefreshListener {
            viewModel.updateRoadPlaces(roadName ?: "")
            binding.swipeRefreshLayoutForRoadPlacesList.isRefreshing = false
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

}