package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        holder.onBind(::deleteCurrentElement, currentList[position])
    }

    private fun deleteCurrentElement(element: PhotoElementModel) {
        //Да, я прекрасно понимаю, что выглядит как костыль, но альтернативы мне кажутся хуже.
        //Тем более список максимум на 5 элементов.
        this.submitList(currentList.toMutableList()
            .also
            {
                it.remove(element)
            }
        )
    }

    class PhotoElementViewHolder(val photoElementBinding: PhotoElementBinding) :
        RecyclerView.ViewHolder(photoElementBinding.root) {
        fun onBind(
            removeCurrentElementCallBack: (PhotoElementModel) -> Unit,
            photoElementModel: PhotoElementModel
        ) {
            Glide.with(photoElementBinding.root)
                .load(photoElementModel.uri)
                .override(dpToPx(60), dpToPx(60))
                .centerCrop()
                .into(photoElementBinding.chosenPhotoPreview)

            photoElementBinding.deleteChosenPhotoButton.setOnClickListener {
                removeCurrentElementCallBack(photoElementModel)
            }
        }

        private fun dpToPx(dp: Int): Int =
            (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

}