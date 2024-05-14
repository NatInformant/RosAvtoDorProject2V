package com.example.rosavtodorproject2.ui.view.roadInformationFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.databinding.RoadAdvertisementElementBinding
import com.example.rosavtodorproject2.ui.view.mainFragment.AdvertisementsDiffUtil

class RoadAdvertisementsListAdapter (
    advertisementsDiffUtil: AdvertisementsDiffUtil,
) : ListAdapter<Advertisement,  RoadAdvertisementsListAdapter.RoadAdvertisementElementViewHolder>(advertisementsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadAdvertisementElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val roadAdvertisementElementBinding = RoadAdvertisementElementBinding.inflate(layoutInflater, parent, false)

        return RoadAdvertisementElementViewHolder(
            roadAdvertisementElementBinding
        )
    }

    override fun onBindViewHolder(holder: RoadAdvertisementElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class RoadAdvertisementElementViewHolder(
        private val roadAdvertisementElementBinding: RoadAdvertisementElementBinding
    ) : RecyclerView.ViewHolder(roadAdvertisementElementBinding.root) {
        fun onBind(advertisement: Advertisement) {
            roadAdvertisementElementBinding.advertisementTitle.text = advertisement.title
            roadAdvertisementElementBinding.advertisementDescription.text = advertisement.description
        }
    }
}