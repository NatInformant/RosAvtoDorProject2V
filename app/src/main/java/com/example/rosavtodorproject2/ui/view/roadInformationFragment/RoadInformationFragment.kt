package com.example.rosavtodorproject2.ui.view.roadInformationFragment

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.example.rosavtodorproject2.ui.view.mainFragment.AdvertisementsDiffUtil
import com.example.rosavtodorproject2.ui.view.mainFragment.AreaAdvertisementsDiffUtil
import com.example.rosavtodorproject2.ui.view.mainFragment.AreaAdvertisementsListAdapter
import com.example.rosavtodorproject2.ui.view.roadsChooseFragment.RoadsChooseFragmentDirections

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

        if (resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT) {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(resources.getInteger(R.integer.know_road_information_panel_scroll_view_height_landscape_orientation))
            )
            binding.knowRoadInformationPanelScrollView.layoutParams = layoutParams
        }

        viewModel.updateRoadAdvertisements(roadName?:"")

        setUpRoadAdvertisementsList()
        //надо поменять
        binding.roadWarningsTitle.text = "Предупреждения по трассе: $roadName"
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToInteractiveMapFragmentPanel.root.setOnClickListener {
            val action = RoadInformationFragmentDirections.actionRoadInformationFragmentToInteractiveMapFragment(
                roadName?:""
            )
            findNavController().navigate(action)
        }
        binding.backToRoadsChooseFragmentPanelButton.setOnClickListener {
            findNavController().navigate(R.id.action_roadInformationFragment_to_roadsChooseFragment)
        }

        binding.knowRoadCafeInformation.setOnClickListener {

        }
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
    private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
}