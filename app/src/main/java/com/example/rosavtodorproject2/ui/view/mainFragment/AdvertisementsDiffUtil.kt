package com.example.rosavtodorproject2.ui.view.mainFragment

import androidx.recyclerview.widget.DiffUtil
import com.example.rosavtodorproject2.data.models.Advertisement

class AdvertisementsDiffUtil : DiffUtil.ItemCallback<Advertisement>() {
    override fun areItemsTheSame(oldItem: Advertisement, newItem: Advertisement): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Advertisement, newItem: Advertisement): Boolean {
        return oldItem == newItem
    }
}