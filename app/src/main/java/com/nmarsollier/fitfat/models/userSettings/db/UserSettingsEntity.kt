package com.nmarsollier.fitfat.models.userSettings.db

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
    val displayName: String = "",

    @ColumnInfo(name = "birth_date")
    val birthDate: Date = Date(),

    @ColumnInfo(name = "weight")
    val weight: Double = 0.0,

    @ColumnInfo(name = "height")
    val height: Double = 0.0,

    @ColumnInfo(name = "sex_type")
    val sex: SexType = SexType.MALE,

    @ColumnInfo(name = "measure_system")
    val measureSystem: MeasureType = MeasureType.METRIC,

    @ColumnInfo(name = "firebase_token")
    val firebaseToken: String? = null
) : Parcelable {
    companion object
}

enum class SexType {
    MALE, FEMALE
}

enum class MeasureType {
    METRIC, IMPERIAL;
}
