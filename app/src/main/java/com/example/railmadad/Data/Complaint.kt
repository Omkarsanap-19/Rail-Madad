package com.example.railmadad.Data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Complaint(
    var ID: String?=null,
    var Tttle: String?=null,
    var Category: String?=null,
    var pnr : String?=null,
    var Incident_Date: String?=null,
    var Status: String?=null,
    var Description : String? =null,
    var Image : List<String>?=null,
    var Audio: String?=null,
    var Video: String?=null,
    var Date: String?=null,
    var Files: MutableList<String>?=null,
    var attachData: MutableList<Image> = mutableListOf(),
    var userId: String? = " ",
): Parcelable
