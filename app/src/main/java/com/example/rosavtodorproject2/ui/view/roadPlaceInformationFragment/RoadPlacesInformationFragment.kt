package com.example.rosavtodorproject2.ui.view.roadPlaceInformationFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rosavtodorproject2.R

class RoadPlacesInformationFragment : Fragment() {

    companion object {
        fun newInstance() = RoadPlacesInformationFragment()
    }

    private lateinit var viewModel: RoadPlacesInformationFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.road_places_information_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RoadPlacesInformationFragmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}