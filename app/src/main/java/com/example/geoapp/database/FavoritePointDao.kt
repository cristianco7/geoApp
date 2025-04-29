package com.example.geoapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.geoapp.models.FavoritePoint

@Dao
interface FavoritePointDao {

    @Query("SELECT * FROM favorite_points")
    suspend fun getAllFavoritePoints(): List<FavoritePoint>

    @Insert
    suspend fun insertFavoritePoint(favoritePoint: FavoritePoint): Long
}