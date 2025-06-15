package com.example.railmadad.Data

import android.os.Parcelable
import androidx.room.Entity
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "complaint_table")
data class Update(
    @androidx.room.PrimaryKey
    var ID: String = "",
    var Tttle: String?=null,
    var Category: String?=null,
    var Date: String?=null,
    var Status: String?=null
): Parcelable
