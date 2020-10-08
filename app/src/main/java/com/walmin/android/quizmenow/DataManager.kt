package com.walmin.android.quizmenow

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataManager(context: Context) {

    private val dataStore = context.createDataStore("GamePreferences")

    companion object {
        val THEME_KEY = preferencesKey<Boolean>("theme")

        val COINS_KEY = preferencesKey<Int>("coins")

        val GAMES_KEY = preferencesKey<Int>("games")
        val CORRECT_KEY = preferencesKey<Int>("correct")
        val INCORRECT_KEY = preferencesKey<Int>("incorrect")

        val QUIZ_LIST_KEY = preferencesKey<String>("quizList")

    }

    suspend fun saveTheme(theme: Boolean) {
        dataStore.edit {
            it[THEME_KEY] = theme
        }
    }

    suspend fun saveCoins(coins: Int) {
        dataStore.edit {
            it[COINS_KEY] = coins
        }
    }

    suspend fun saveGame(games: Int, correct: Int, incorrect: Int){
        dataStore.edit {
            it[GAMES_KEY] = games
            it[CORRECT_KEY] = correct
            it[INCORRECT_KEY] = incorrect
        }

    }

    suspend fun saveQuizList(quizList: String) {
        dataStore.edit {
            it[QUIZ_LIST_KEY] = quizList
        }
    }

    val themeFlow: Flow<Boolean> = dataStore.data.map { it[THEME_KEY] ?: true }

    val coinsFlow: Flow<Int> = dataStore.data.map { it[COINS_KEY] ?: 10 }

    val gamesFlow: Flow<Int> = dataStore.data.map { it[GAMES_KEY] ?: 0 }
    val correctFlow: Flow<Int> = dataStore.data.map { it[CORRECT_KEY] ?: 0 }
    val incorrectFlow: Flow<Int> = dataStore.data.map { it[INCORRECT_KEY] ?: 0 }

    val quizListFlow: Flow<String> = dataStore.data.map { it[QUIZ_LIST_KEY] ?: "0u|1u|2u|3u|4u|5u|6u|7u|8u|9l|10l|11l|12l|13l|14l|15l|16l|17l|18l|19l|20l|21l|22l|23l|" }

}