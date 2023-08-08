package com.nmarsollier.fitfat.model.userSettings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings LIMIT 1")
    fun findCurrent(): UserSettingsEntity?

    @Insert
    fun insert(user: UserSettingsEntity)

    @Update
    fun update(user: UserSettingsEntity)
}
