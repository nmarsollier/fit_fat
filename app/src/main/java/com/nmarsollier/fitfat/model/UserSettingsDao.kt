package com.nmarsollier.fitfat.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings LIMIT 1")
    fun findCurrent(): Flow<UserSettings?>

    @Insert
    fun insert(user: UserSettings)

    @Update
    fun update(user: UserSettings)
}
