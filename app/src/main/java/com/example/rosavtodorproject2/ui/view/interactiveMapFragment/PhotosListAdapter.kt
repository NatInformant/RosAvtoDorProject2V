package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.databinding.PhotoElementBinding
import com.example.rosavtodorproject2.ui.model.PhotoElementModel

class PhotosListAdapter(
    photosDiffUtil: PhotosDiffUtil
) : ListAdapter<PhotoElementModel, PhotosListAdapter.PhotoElementViewHolder>(photosDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoElementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val photoElementBinding = PhotoElementBinding.inflate(layoutInflater, parent, false)

        return PhotoElementViewHolder(photoElementBinding)
    }

    override fun onBindViewHolder(holder: PhotoElementViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class PhotoElementViewHolder(val photoElementBinding: PhotoElementBinding) :
        RecyclerView.ViewHolder(photoElementBinding.root) {
        fun onBind(photoElementModel: PhotoElementModel) {
            photoElementBinding.chosenPhotoPreview.setImageURI(photoElementModel.uri)
        }
    }


}