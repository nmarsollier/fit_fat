package com.nmarsollier.fitfat.model.userSettings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.toCm
import com.nmarsollier.fitfat.utils.toInch
import com.nmarsollier.fitfat.utils.toKg
import com.nmarsollier.fitfat.utils.toPounds
import kotlinx.android.parcel.Parcelize
import java.util.*

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
    var measureSystem: MeasureType = MeasureType.METRIC,

    @ColumnInfo(name = "firebase_token")
    var firebaseToken: String? = null
) : Parcelable {
    fun isNew() = weight == 0.0 || height == 0.0
}

enum class SexType {
    MALE, FEMALE
}

enum class MeasureType(val heightResId: Int, val weightResId: Int) {
    METRIC(R.string.unit_cm, R.string.unit_kg),
    IMPERIAL(R.string.unit_in, R.string.unit_lb);

    fun standardWeight(value: Double): Double {
        return if (this == IMPERIAL) {
            value.toKg()
        } else {
            value
        }
    }

    fun standardWidth(value: Double): Double {
        return if (this == IMPERIAL) {
            value.toCm()
        } else {
            value
        }
    }


    fun displayWeight(value: Double): Double {
        return if (this == IMPERIAL) {
            value.toPounds()
        } else {
            value
        }
    }

    fun displayHeight(value: Double): Double {
        return if (this == IMPERIAL) {
            value.toInch()
        } else {
            value
        }
    }
}

