package com.example.rosavtodorproject2.ui.model

import android.net.Uri
import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class PhotoElementModel(
    val uri: Uri,
) :Parcelable