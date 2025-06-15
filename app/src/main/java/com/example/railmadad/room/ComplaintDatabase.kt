package com.example.railmadad.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(entities = [com.example.railmadad.Data.Update::class], version = 1, exportSchema = false)
abstract class ComplaintDatabase: RoomDatabase() {

    abstract fun trackDao():trackDao

    companion object{

        @Volatile
        private var INSTANCE : ComplaintDatabase? = null

        fun getDatabase(context: Context):ComplaintDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ComplaintDatabase::class.java,
                    "user_database"
                ).build()

                INSTANCE = instance

                return instance
            }
        }
    }
}