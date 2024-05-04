package com.example.rosavtodorproject2.ui.view.mainFragment

import androidx.recyclerview.widget.DiffUtil
import com.example.rosavtodorproject2.data.models.Advertisement

class AreaAdvertisementsDiffUtil : DiffUtil.ItemCallback<Pair<String,List<Advertisement>>>() {
    override fun areItemsTheSame(oldItem: Pair<String,List<Advertisement>>, newItem: Pair<String,List<Advertisement>>): Boolean {
        return oldItem.first == newItem.first
    }

    override fun areContentsTheSame(oldItem: Pair<String,List<Advertisement>>, newItem: Pair<String,List<Advertisement>>): Boolean {
        return oldItem == newItem
    }
}