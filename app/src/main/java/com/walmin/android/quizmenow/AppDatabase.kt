package com.walmin.android.quizmenow

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Category::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "db-categories")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance

                }
                return instance

            }

        }

    }

}