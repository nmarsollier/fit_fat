package com.nmarsollier.fitfat.models.userSettings.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface UserSettingsDao {
    @Query("SELECT * FROM user_settings LIMIT 1")
    suspend fun findCurrent(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(user: UserSettingsEntity)
}
