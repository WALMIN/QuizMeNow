package com.walmin.android.quizmenow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    companion object MainActivity {
        var homeList = mutableListOf<Category>()

        lateinit var dataManager: DataManager

        var music = true
        var sound = true

        var coins = 0

        var games = 0
        var correct = 0.0
        var incorrect = 0.0

    }

    // Database
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Database
        job = Job()
        db = AppDatabase.getInstance(this)

        // Saved data
        dataManager = DataManager(this)
        observeData()

    }

    // Get saved data
    private fun observeData(){
        dataManager.firstRunFlow.asLiveData().observe(this, {
            // Add categories to room database if first run
            if(it){
                GlobalScope.launch {
                    dataManager.saveFirstRun(false)

                }

                // Offline categories
                saveCategory(Category(0,"Animals", "0", "F44336", "0", false))
                saveCategory(Category(1, "Art", "1", "9C27B0", "0", false))
                saveCategory(Category(2, "Books", "2", "3F51B5", "0", false))
                saveCategory(Category(3, "General knowledge", "3", "03A9F4", "0", false))
                saveCategory(Category(4, "Geography", "4", "009688", "0", false))
                saveCategory(Category(5, getString(R.string.goOnline), "-1", "8BC34A", "0", false))

                // Online categories
                saveCategory(Category(6, "Animals", "27", "F44336","0", false))
                saveCategory(Category(7, "Art", "25", "9C27B0", "0", false))
                saveCategory(Category(8, "Books", "10", "3F51B5", "0", false))
                saveCategory(Category(9, "General knowledge", "9", "03A9F4", "0", false))
                saveCategory(Category(10, "Geography", "22", "009688", "0", false))
                saveCategory(Category(11, "Film", "11", "8BC34A", "0", false))
                saveCategory(Category(12, "History", "23", "FFEB3B", "0", false))
                saveCategory(Category(13, "Music", "12", "FF9800", "0", false))
                saveCategory(Category(14, "Sports", "21", "795548", "0", false))

                saveCategory(Category(15, "Board Games", "16", "E32F2F", "10", true))
                saveCategory(Category(16, "Cartoon & Animations", "32", "7B1FA2", "10", true))
                saveCategory(Category(17, "Celebrities", "26", "303F9F", "10", true))

                saveCategory(Category(18, "Comics", "29", "0288D1", "15", true))
                saveCategory(Category(19, "Computers", "18", "00796B", "15", true))
                saveCategory(Category(20, "Gadgets", "30", "689F38", "15", true))

                saveCategory(Category(21, "Japanese Anime & Manga", "31", "FBC02D", "20", true))
                saveCategory(Category(22, "Mathematics", "19", "F57C00", "20", true))
                saveCategory(Category(23, "Musicals & Theatres", "13", "5D4037", "20", true))

                saveCategory(Category(24, "Mythology", "20", "B71C1C", "25", true))
                saveCategory(Category(25, "Politics", "24", "4A148C", "25", true))
                saveCategory(Category(26, "Science & Nature", "17", "1A237E", "25", true))

                saveCategory(Category(27, "Television", "14", "01579B", "30", true))
                saveCategory(Category(28, "Vehicles", "28", "004D40", "30", true))
                saveCategory(Category(29, "Video Games", "15", "33691E", "30", true))
            }

        })

        dataManager.musicFlow.asLiveData().observe(this, { music = it })
        dataManager.soundFlow.asLiveData().observe(this, { sound = it })

        dataManager.coinsFlow.asLiveData().observe(this, { coins = it })

        dataManager.gamesFlow.asLiveData().observe(this, { games = it })
        dataManager.correctFlow.asLiveData().observe(this, { correct = it.toDouble() })
        dataManager.incorrectFlow.asLiveData().observe(this, { incorrect = it.toDouble() })

    }

    // Save category item in room db
    fun saveCategory(category: Category){
        launch(Dispatchers.IO) {
            db.categoryDao().insert(category)

        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()

    }

}