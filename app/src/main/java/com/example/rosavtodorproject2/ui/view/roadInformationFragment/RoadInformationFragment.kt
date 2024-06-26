package com.example.rosavtodorproject2.ui.view.roadInformationFragment

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.databinding.RoadInformationFragmentBinding
import com.example.rosavtodorproject2.ioc.MainViewModelFactory
import com.example.rosavtodorproject2.ioc.RoadInformationViewModelFactory
import com.example.rosavtodorproject2.ioc.applicationInstance
import com.example.rosavtodorproject2.ui.view.mainFragment.AdvertisementsDiffUtil
import javax.inject.Inject

class RoadInformationFragment : Fragment() {

    private lateinit var binding: RoadInformationFragmentBinding

    private var adapter: RoadAdvertisementsListAdapter = RoadAdvertisementsListAdapter(
        advertisementsDiffUtil = AdvertisementsDiffUtil(),
    )
    @Inject
    lateinit var viewModelFactory: RoadInformationViewModelFactory
    private val viewModel: RoadInformationFragmentViewModel by viewModels { viewModelFactory }
    var roadName: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.applicationInstance.applicationComponent.injectRoadInformationFragment(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoadInformationFragmentBinding.inflate(layoutInflater, container, false)
        roadName = arguments?.getString("roadName")

        if (resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT) {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(resources.getInteger(R.integer.know_road_information_panel_scroll_view_height_landscape_orientation))
            )
            binding.knowRoadInformationPanelScrollView.layoutParams = layoutParams
        }

        binding.roadWarningsTitle.text =
            getString(R.string.road_warnings_title_format, roadName)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateRoadAdvertisements(roadName ?: "")

        setUpRoadAdvertisementsList()

        binding.goToInteractiveMapFragmentPanel.root.setOnClickListener {
            findNavController().navigate(R.id.action_roadInformationFragment_to_interactiveMapFragment)
        }
        binding.backToRoadsChooseFragmentPanelButton.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.knowRoadCafeInformation.setOnClickListener {
            knowRoadInformationButtonsListener("Cafe", R.string.cafe_road_places_list_title)
        }
        binding.knowRoadGuesthouseInformation.setOnClickListener {
            knowRoadInformationButtonsListener("Hotel", R.string.guesthouse_road_places_list_title)
        }
        binding.knowRoadPetrolStationInformation.setOnClickListener {
            knowRoadInformationButtonsListener(
                "GasStation",
                R.string.petrol_station_road_places_list_title
            )
        }
        binding.knowRoadCarServiceInformation.setOnClickListener {
            knowRoadInformationButtonsListener(
                "CarService",
                R.string.car_service_road_places_list_title
            )
        }
        binding.knowRoadCarRechargeStationInformation.setOnClickListener {
            knowRoadInformationButtonsListener(
                "ElectricFillingStation",
                R.string.car_recharge_station_road_places_list_title
            )
        }
        binding.knowRoadEventsInformation.setOnClickListener {
            knowRoadInformationButtonsListener("Event", R.string.event_road_places_list_title)
        }
    }

    private fun knowRoadInformationButtonsListener(
        roadPlaceType: String,
        roadPlacesListTitleStringResource: Int
    ) {
        val action =
            RoadInformationFragmentDirections.actionRoadInformationFragmentToRoadPlacesInformationFragment(
                roadName ?: "",
                roadPlaceType,
                roadPlacesListTitleStringResource
            )
        findNavController().navigate(action)
    }

    private fun setUpRoadAdvertisementsList() {
        val roadsListRecyclerView: RecyclerView =
            binding.roadAdvertisementsRecyclerList

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

            binding.swipeRefreshLayoutForRoadAdvertisementsList.isRefreshing = false

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

        binding.swipeRefreshLayoutForRoadAdvertisementsList.setOnRefreshListener {
            viewModel.updateRoadAdvertisements(roadName ?: "")
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
}