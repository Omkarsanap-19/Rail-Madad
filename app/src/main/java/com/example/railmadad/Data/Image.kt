package com.example.railmadad.Data

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Image(
    var url: String? = null,
    var metadata: List<String>? = null,
    var keywords: List<String>? = null,
    var ocrText: String? = null,
    var fileName: String? = null
):Parcelable
