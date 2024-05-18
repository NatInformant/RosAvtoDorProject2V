package com.example.rosavtodorproject2.ui.view.roadPlaceInformationFragment

import android.content.Intent
import android.net.Uri
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
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.databinding.RoadPlacesInformationFragmentBinding

class RoadPlacesInformationFragment : Fragment() {

    private lateinit var binding: RoadPlacesInformationFragmentBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private var adapter: RoadPlacesListAdapter = RoadPlacesListAdapter(
        roadPlacesDiffUtil = RoadPlacesDiffUtil(),
        getRoadPlaceTitle = ::getRoadPlaceTitle,
        getDistanceToRoadPlace = ::getDistanceToRoadPlace,
        onRoadPlaceItemButtonClick = ::goToYandexMapButtonClick,
    )

    private val viewModel: RoadPlacesInformationFragmentViewModel by viewModels { applicationComponent.getRoadPlacesInformationViewModelFactory() }
    private var roadName: String? = null
    private var roadPlaceType: String? = null
    private val BASE_LATITUDE: Double = 55.154
    private val BASE_LONGITUDE: Double = 61.4291
    private val roadPlaceTypeToNameResource: Map<String, Int> = mapOf(
        Pair("Cafe", R.string.cafe),
        Pair("Hotel", R.string.guesthouse),
        Pair("GasStation", R.string.petrol_station),
        Pair("CarService", R.string.car_service),
        Pair("ElectricFillingStation", R.string.car_recharge_station),
        Pair("Event", R.string.event),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoadPlacesInformationFragmentBinding.inflate(layoutInflater, container, false)
        roadName = arguments?.getString("roadName")
        roadPlaceType = arguments?.getString("roadPlaceType")
        val roadPlacesListTitleStringResource =
            arguments?.getInt("roadPlacesListTitleStringResource")

        viewModel.updateRoadPlaces(
            roadName ?: "",
            roadPlaceType ?: "",
            Coordinates(BASE_LATITUDE, BASE_LONGITUDE)
        )

        setUpRoadPlacesList()

        binding.roadPlacesListTitle.text =
            getString(roadPlacesListTitleStringResource ?: R.string.error_road_places_list_title)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToRoadInformationFragmentPanelButton.setOnClickListener {
            val action =
                RoadPlacesInformationFragmentDirections.actionRoadPlacesInformationFragmentToRoadInformationFragment(
                    roadName ?: ""
                )
            findNavController().navigate(action)
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
            viewModel.updateRoadPlaces(
                roadName ?: "",
                roadPlaceType ?: "",
                Coordinates(BASE_LATITUDE, BASE_LONGITUDE)
            )
            binding.swipeRefreshLayoutForRoadPlacesList.isRefreshing = false
        }
    }

    private fun getRoadPlaceTitle(roadPlaceName: String) =
        getString(
            R.string.verified_point_name_format,
            getString(roadPlaceTypeToNameResource[roadPlaceType]!!),
            roadPlaceName
        )

    private fun getDistanceToRoadPlace(distance: Double) =
        getString(
            R.string.distance_to_road_place_format,
            distance
        )

    private fun goToYandexMapButtonClick(currentRoadPlaceCoordinates: Coordinates) {
        //пока не нужно
        /*if (App.getInstance().currentUserPosition == null) {
            Toast
                .makeText(
                    requireContext(),
                    "не зная вашего местоположения мы не можем работать",
                    Toast.LENGTH_SHORT
                ).show()
            return
        }*/
        val url =
            "https://yandex.ru/maps/?rtext=" +
                    //пока так, потом верну как было
                    "${BASE_LATITUDE}," +// точка
                    "${BASE_LONGITUDE}" +// начала пути
                    "~" +
                    "${currentRoadPlaceCoordinates.latitude}," +//точка
                    "${currentRoadPlaceCoordinates.longitude}" +//конца пути
                    "&rtt=auto"

        val intent = Intent(Intent.ACTION_VIEW) // Создаем новый Intent

        intent.data = Uri.parse(url) // Устанавливаем URL-адрес для Intent

        startActivity(intent) // Запускаем новое Activity с помощью Intent
    }
}