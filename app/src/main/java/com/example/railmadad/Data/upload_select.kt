package com.example.railmadad.Data

import android.os.Parcelable
import android.widget.ImageView
import kotlinx.parcelize.Parcelize

@Parcelize
data class upload_select(
    val img : Int,
    val name: String
): Parcelable