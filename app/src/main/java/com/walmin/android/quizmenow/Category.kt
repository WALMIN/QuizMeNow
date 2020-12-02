package com.walmin.android.quizmenow

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_categories")
data class Category(@PrimaryKey(autoGenerate = false) val id: Int,
                    @ColumnInfo(name = "title") var title: String,
                    @ColumnInfo(name = "value") var value: String,
                    @ColumnInfo(name = "color") var color: String,
                    @ColumnInfo(name = "price") var price: String,
                    @ColumnInfo(name = "locked") var locked: Boolean)

