package com.walmin.android.quizmenow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object MainActivity {
        var coins = 100

        var games = 1
        var correct = 6.0
        var incorrect = 4.0

        var unlocked = "0u|1u|2u|3u|4u|5u|6u|7u|8u|9l|10l|11l|12l|13l|14l|15l|16l|17l|18l|19l|20l|21l|22l|23l|"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}