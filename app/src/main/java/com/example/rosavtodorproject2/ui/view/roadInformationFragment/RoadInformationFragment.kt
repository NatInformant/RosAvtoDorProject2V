package com.example.rosavtodorproject2.ui.view.roadInformationFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.databinding.RoadInformationFragmentBinding
import com.example.rosavtodorproject2.databinding.RoadsChooseFragmentBinding

class RoadInformationFragment : Fragment() {

    private lateinit var binding: RoadInformationFragmentBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoadInformationFragmentBinding.inflate(layoutInflater, container, false)
        val roadName = arguments?.getString("roadName")
        Toast.makeText(requireContext(),roadName,Toast.LENGTH_LONG).show()
        return binding.root
    }


}