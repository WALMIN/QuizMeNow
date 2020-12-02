package com.walmin.android.quizmenow

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataManager(context: Context) {

    private val dataStore = context.createDataStore("GamePreferences")

    companion object {
        val FIRST_RUN_KEY = preferencesKey<Boolean>("firstRun")

        val MUSIC_KEY = preferencesKey<Boolean>("music")
        val SOUND_KEY = preferencesKey<Boolean>("sound")

        val COINS_KEY = preferencesKey<Int>("coins")

        val GAMES_KEY = preferencesKey<Int>("games")
        val CORRECT_KEY = preferencesKey<Int>("correct")
        val INCORRECT_KEY = preferencesKey<Int>("incorrect")

        val QUIZ_LIST_KEY = preferencesKey<String>("quizList")

    }

    suspend fun saveFirstRun(firstRun: Boolean) {
        dataStore.edit {
            it[FIRST_RUN_KEY] = firstRun
        }
    }

    suspend fun saveMusic(music: Boolean) {
        dataStore.edit {
            it[MUSIC_KEY] = music
        }
    }

    suspend fun saveSound(sound: Boolean) {
        dataStore.edit {
            it[SOUND_KEY] = sound
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

    val firstRunFlow: Flow<Boolean> = dataStore.data.map { it[FIRST_RUN_KEY] ?: true }

    val musicFlow: Flow<Boolean> = dataStore.data.map { it[MUSIC_KEY] ?: true }
    val soundFlow: Flow<Boolean> = dataStore.data.map { it[SOUND_KEY] ?: true }

    val coinsFlow: Flow<Int> = dataStore.data.map { it[COINS_KEY] ?: 10 }

    val gamesFlow: Flow<Int> = dataStore.data.map { it[GAMES_KEY] ?: 0 }
    val correctFlow: Flow<Int> = dataStore.data.map { it[CORRECT_KEY] ?: 0 }
    val incorrectFlow: Flow<Int> = dataStore.data.map { it[INCORRECT_KEY] ?: 0 }

}