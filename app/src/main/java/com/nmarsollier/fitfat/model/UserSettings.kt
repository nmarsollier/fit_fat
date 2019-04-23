package com.nmarsollier.fitfat.model

import androidx.room.*
import java.util.*

@Dao
abstract class UserSettingsDao {
    @Query("SELECT * FROM user_settings LIMIT 1")
    abstract fun findUserSettings(): UserSettings?

    @Insert
    abstract fun insert(user: UserSettings)

    @Update
    abstract fun update(user: UserSettings)

    @Delete
    abstract fun delete(user: UserSettings)

    fun getUserSettings(): UserSettings {
        var result = findUserSettings()
        if (result == null) {
            result = UserSettings(1).also {
                insert(it)
            }
        }
        return result
    }
}

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val uid: Int
) {

    @ColumnInfo(name = "display_name")
    var displayName: String = ""

    @ColumnInfo(name = "birth_date")
    var birthDate: Date = Date()

    @ColumnInfo(name = "weight")
    var weight: Double = 50.0

    @ColumnInfo(name = "height")
    var height: Double = 160.0

    @ColumnInfo(name = "sex_type")
    var sex: SexType = SexType.MALE

    @ColumnInfo(name = "measure_system")
    var measureSystem: MeasureType = MeasureType.METRIC
}

enum class SexType {
    MALE, FEMALE
}

enum class MeasureType {
    METRIC, IMPERIAL
}

