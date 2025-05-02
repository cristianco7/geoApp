package com.example.geoapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.geoapp.models.FavoritePoint

@Dao
interface FavoritePointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePoint(favoritePoint: FavoritePoint)

    @Query("SELECT * FROM favorite_points WHERE isAlertPoint = 1")
    suspend fun getAlertPoints(): List<FavoritePoint>

    @Query("SELECT * FROM favorite_points")
    suspend fun getAllFavoritePoints(): List<FavoritePoint>

    @Query("SELECT * FROM favorite_points WHERE latitude = :lat AND longitude = :lng AND isAlertPoint = 1")
    suspend fun getAlertPointByLocation(lat: Double, lng: Double): FavoritePoint?
}