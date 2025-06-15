package com.example.railmadad.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface trackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(complaint: com.example.railmadad.Data.Update)

    @Query("SELECT * FROM complaint_table ORDER BY ID ASC")
    fun readAllData(): LiveData<MutableList<com.example.railmadad.Data.Update>>


}