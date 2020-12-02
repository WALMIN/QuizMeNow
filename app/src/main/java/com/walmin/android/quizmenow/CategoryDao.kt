package com.walmin.android.quizmenow

import androidx.room.*

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Query("SELECT * FROM table_categories")
    fun getAll(): List<Category>

}