package com.example.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app.data.local.entities.QuoteSettingsEntity
import kotlinx.coroutines.flow.Flow

/*************** DAO de configuración ***************/
@Dao
interface SettingsDao {

    @Query("SELECT * FROM quote_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<QuoteSettingsEntity?>

    @Query("SELECT * FROM quote_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): QuoteSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: QuoteSettingsEntity)
}