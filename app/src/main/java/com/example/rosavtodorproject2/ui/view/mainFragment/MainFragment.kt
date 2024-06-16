package com.example.rosavtodorproject2.ui.view.mainFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.databinding.MainFragmentBinding
import com.example.rosavtodorproject2.ioc.MainViewModelFactory
import com.example.rosavtodorproject2.ioc.applicationInstance
import com.example.rosavtodorproject2.service.AlertsMessagingService
import javax.inject.Inject

class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding


    private var adapter: AreaAdvertisementsListAdapter = AreaAdvertisementsListAdapter(
        areaAdvertisementsDiffUtil = AreaAdvertisementsDiffUtil(),
    )

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainFragmentViewModel by viewModels { viewModelFactory }
    private var optionsMenu: Menu? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.applicationInstance.applicationComponent.injectMainFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater, container, false)

        setUpToolBar()

        return binding.root
    }

    private fun setUpToolBar() {
        val navController = NavHostFragment.findNavController(this)

        setUpSideBar(navController)

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

    private fun setUpSideBar(
        navController: NavController
    ) {
        val sideBar = binding.navView

        optionsMenu = sideBar.menu
        sideBar.menu.children.filter { it.actionView is CheckBox }.forEach { menuItem ->
            (menuItem.actionView as CheckBox).also {
                if (menuItem.title!!.contains("области")) {
                    it.isChecked =
                        AlertsMessagingService
                            .obl_name_to_filer_value
                            .getValue(menuItem.title.toString())
                } else {
                    it.isChecked =
                        AlertsMessagingService
                            .road_name_to_filer_value
                            .getValue(menuItem.title.toString())
                    menuItem.title = "    " + menuItem.title
                }
            }.setOnClickListener {
                menuItemCheckBoxListener(menuItem)
            }
        }

        sideBar.setupWithNavController(navController)
    }

    private fun menuItemCheckBoxListener(item: MenuItem) {
        val currentState = (item.actionView as CheckBox).isChecked
        if (item.title!!.contains("области")) {
            AlertsMessagingService
                .obl_name_to_filer_value[item.title.toString().trim()] = currentState
            for (x in optionsMenu!!.children) {
                if (x.order > item.order) {
                    if (x.title!!.contains("области") or x.title!!.contains("О программе")) {
                        break
                    }
                    (x.actionView as CheckBox).isChecked = currentState
                    AlertsMessagingService
                        .road_name_to_filer_value[item.title.toString().trim()] = currentState
                }
            }
        } else {
            AlertsMessagingService
                .road_name_to_filer_value[item.title.toString().trim()] = currentState
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdvertisementsList()

        binding.goToRoadsChooseFragmentPanel.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_roadsChooseFragment)
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

        viewModel.advertisements.observe(viewLifecycleOwner) { httpResponseState ->
            //Для справки самому себе - в первый раз мы проваливаемся сюда
            //НЕ при изменении значения, за которым следим, а когда фрагмент инициализировался,
            //а уже дальше проваливаемся только при изменениях!

            binding.swipeRefreshLayoutForAdvertisementsList.isRefreshing = false

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
        allAreasAdvertisementsRecyclerView.addItemDecoration(
            AreaAdvertisementsItemDecoration(topOffset = 10)
        )

        binding.swipeRefreshLayoutForAdvertisementsList.setOnRefreshListener {
            viewModel.updateAdvertisements()
        }
    }
}
