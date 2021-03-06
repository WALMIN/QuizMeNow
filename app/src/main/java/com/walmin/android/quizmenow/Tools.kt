package com.walmin.android.quizmenow

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import com.google.android.material.snackbar.Snackbar

object Tools {

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true

            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true

            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true

            }

        }
        return false

    }

    fun getImage(context: Context, imageName: String?): Int {
        return context.resources.getIdentifier(imageName, "drawable", context.packageName)

    }

    fun showSnackbar(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()

    }

}