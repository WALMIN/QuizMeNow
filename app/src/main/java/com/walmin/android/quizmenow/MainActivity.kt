package com.walmin.android.quizmenow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData

class MainActivity : AppCompatActivity() {

    companion object MainActivity {
        lateinit var dataManager: DataManager

        var coins = 0

        var games = 0
        var correct = 0.0
        var incorrect = 0.0

        var quizList = ""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        dataManager = DataManager(this)
        observeData()

    }

    private fun observeData(){
        dataManager.coinsFlow.asLiveData().observe(this, { coins = it })

        dataManager.gamesFlow.asLiveData().observe(this, { games = it })
        dataManager.correctFlow.asLiveData().observe(this, { correct = it.toDouble() })
        dataManager.incorrectFlow.asLiveData().observe(this, { incorrect = it.toDouble() })

        dataManager.quizListFlow.asLiveData().observe(this, { quizList = it })

    }

}