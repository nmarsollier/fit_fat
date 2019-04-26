package com.nmarsollier.fitfat.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*

private var USER_SETTINGS: UserSettings? = null

@Dao
abstract class UserSettingsDao {
    @Query("SELECT * FROM user_settings LIMIT 1")
    protected abstract fun findUserSettings(): UserSettings?

    @Insert
    protected abstract fun insertUserSettings(user: UserSettings)

    @Update
    protected abstract fun updateUserSettings(user: UserSettings)

    fun getUserSettings(): UserSettings {
        return USER_SETTINGS ?: (findUserSettings() ?: UserSettings(1).also {
            insertUserSettings(it)
        }).also {
            USER_SETTINGS = it
        }
    }

    fun update(user: UserSettings) {
        USER_SETTINGS = null
        updateUserSettings(user)
    }
}

@Parcelize
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val uid: Int,

    @ColumnInfo(name = "display_name")
    var displayName: String = "",

    @ColumnInfo(name = "birth_date")
    var birthDate: Date = Date(),

    @ColumnInfo(name = "weight")
    var weight: Double = 0.0,

    @ColumnInfo(name = "height")
    var height: Double = 0.0,

    @ColumnInfo(name = "sex_type")
    var sex: SexType = SexType.MALE,

    @ColumnInfo(name = "measure_system")
    var measureSystem: MeasureType = MeasureType.METRIC
) : Parcelable {
    fun isNew() = weight == 0.0 || height == 0.0
}

enum class SexType {
    MALE, FEMALE
}

enum class MeasureType {
    METRIC, IMPERIAL
}

