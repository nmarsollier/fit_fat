package com.nmarsollier.fitfat.userSettings.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface UserSettingsDao {
    @Query("SELECT * FROM user_settings LIMIT 1")
    fun findCurrent(): UserSettingsData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(user: UserSettingsData)
}
