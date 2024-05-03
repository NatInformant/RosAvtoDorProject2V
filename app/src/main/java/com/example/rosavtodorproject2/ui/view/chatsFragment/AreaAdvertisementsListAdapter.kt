package com.example.rosavtodorproject2.ui.view.chatsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.databinding.AreaAdvertisementsElementBinding

class AreaAdvertisementsListAdapter(
    areaAdvertisementsDiffUtil: AreaAdvertisementsDiffUtil,
) : ListAdapter<Pair<String,List<Advertisement>>, AreaAdvertisementsListAdapter.AreaAdvertisementsElementViewHolder>(areaAdvertisementsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaAdvertisementsElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val areaAdvertisementsElementBinding = AreaAdvertisementsElementBinding.inflate(layoutInflater, parent, false)

        return AreaAdvertisementsElementViewHolder(
            areaAdvertisementsElementBinding
        )
    }

    override fun onBindViewHolder(holder: AreaAdvertisementsElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class AreaAdvertisementsElementViewHolder(
        private val areaAdvertisementsElementBinding: AreaAdvertisementsElementBinding
    ) : RecyclerView.ViewHolder(areaAdvertisementsElementBinding.root) {

        private var adapter: AdvertisementsListAdapter = AdvertisementsListAdapter(
            advertisementsDiffUtil = AdvertisementsDiffUtil(),
        )
        fun onBind(areaAdvertisements: Pair<String,List<Advertisement>>) {
            areaAdvertisementsElementBinding.areaAdvertisementsTitle.text = areaAdvertisements.first

            val areaAdvertisementsRecyclerView = areaAdvertisementsElementBinding.currentAreaAdvertisementsList
            areaAdvertisementsRecyclerView.adapter = adapter

            val layoutManager = LinearLayoutManager(
                areaAdvertisementsElementBinding.root.context,
                LinearLayoutManager.VERTICAL,
                false
            )

            areaAdvertisementsRecyclerView.layoutManager = layoutManager
            adapter.submitList(areaAdvertisements.second)
        }
    }
}
