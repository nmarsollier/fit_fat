package com.nmarsollier.fitfat.model.userSettings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "user_settings")
data class UserSettingsEntity(
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
    var measureSystem: MeasureType = MeasureType.METRIC,

    @ColumnInfo(name = "firebase_token")
    var firebaseToken: String? = null
) : Parcelable {
    fun isNew() = weight == 0.0 || height == 0.0

    enum class SexType {
        MALE, FEMALE
    }

    enum class MeasureType {
        METRIC, IMPERIAL;
    }

    companion object
}
