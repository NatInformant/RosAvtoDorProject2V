package com.example.rosavtodorproject2.ui.view.chatsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.databinding.AdvertisementElementBinding
import com.example.rosavtodorproject2.databinding.AreaAdvertisementsElementBinding

class AdvertisementsListAdapter (
    advertisementsDiffUtil: AdvertisementsDiffUtil,
) : ListAdapter<Advertisement, AdvertisementsListAdapter.AdvertisementElementViewHolder>(advertisementsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertisementElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val advertisementElementBinding = AdvertisementElementBinding.inflate(layoutInflater, parent, false)

        return AdvertisementElementViewHolder(
            advertisementElementBinding
        )
    }

    override fun onBindViewHolder(holder: AdvertisementElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class AdvertisementElementViewHolder(
        private val advertisementElementBinding: AdvertisementElementBinding
    ) : RecyclerView.ViewHolder(advertisementElementBinding.root) {
        fun onBind(advertisement: Advertisement) {
            advertisementElementBinding.advertisementTitle.text = advertisement.title
            advertisementElementBinding.advertisementDescription.text = advertisement.description
        }
    }
}