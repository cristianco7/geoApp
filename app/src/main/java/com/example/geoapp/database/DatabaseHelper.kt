package com.example.geoapp.database

import android.content.Context
import com.example.geoapp.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseHelper {

    fun <T> loadPoints(
        context: Context,
        query: suspend FavoritePointDao.() -> T,
        onLoaded: (T) -> Unit
    ) {
        val database = AppDatabase.getDatabase(context)
        val favoritePointDao = database.favoritePointDao()

        CoroutineScope(Dispatchers.IO).launch {
            val result = favoritePointDao.query()
            (context as? MainActivity)?.runOnUiThread {
                onLoaded(result)
            }
        }
    }
}