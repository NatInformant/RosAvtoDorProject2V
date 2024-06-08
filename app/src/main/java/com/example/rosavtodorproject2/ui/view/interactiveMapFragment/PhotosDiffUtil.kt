package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.example.rosavtodorproject2.data.models.Road
import com.example.rosavtodorproject2.ui.model.PhotoElementModel

class PhotosDiffUtil : ItemCallback<PhotoElementModel>() {
    override fun areItemsTheSame(oldItem: PhotoElementModel, newItem: PhotoElementModel): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(
        oldItem: PhotoElementModel,
        newItem: PhotoElementModel
    ): Boolean {
        return oldItem == newItem
    }

}