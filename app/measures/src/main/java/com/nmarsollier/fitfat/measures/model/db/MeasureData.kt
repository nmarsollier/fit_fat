package com.nmarsollier.fitfat.measures.model.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nmarsollier.fitfat.measures.R
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
@Entity(tableName = "measures")
data class MeasureData(
    @PrimaryKey val uid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "body_weight") val bodyWeight: Double,
    @ColumnInfo(name = "body_height") val bodyHeight: Double,
    @ColumnInfo(name = "age") val age: Int = 0,
    @ColumnInfo(name = "sex") val sex: UserSettingsData.SexType,
    @ColumnInfo(name = "date") val date: Date = Date(),
    @ColumnInfo(name = "measure_method") val measureMethod: MeasureMethod,
    @ColumnInfo(name = "chest") val chest: Int = 0,
    @ColumnInfo(name = "abdominal") val abdominal: Int = 0,
    @ColumnInfo(name = "thigh") val thigh: Int = 0,
    @ColumnInfo(name = "tricep") val tricep: Int = 0,
    @ColumnInfo(name = "subscapular") val subscapular: Int = 0,
    @ColumnInfo(name = "suprailiac") val suprailiac: Int = 0,
    @ColumnInfo(name = "midaxillary") val midaxillary: Int = 0,
    @ColumnInfo(name = "bicep") val bicep: Int = 0,
    @ColumnInfo(name = "lower_back") val lowerBack: Int = 0,
    @ColumnInfo(name = "calf") val calf: Int = 0,
    @ColumnInfo(name = "fat_percent") val fatPercent: Double = 0.0,
    @ColumnInfo(name = "cloud_sync") val cloudSync: Boolean = false
) : Parcelable {
    companion object
}


enum class MeasureMethod {
    JACKSON_POLLOCK_7,
    JACKSON_POLLOCK_3,
    JACKSON_POLLOCK_4,
    PARRILLO,
    DURNIN_WOMERSLEY,
    FROM_SCALE,
    WEIGHT_ONLY;
}

enum class MeasureValue(
    val titleRes: Int,
    val requiredFor: List<MeasureMethod>,
    val maxScale: Int,
    val inputType: InputType,
    val unitType: UnitType
) {
    BODY_WEIGHT(
        titleRes = R.string.measure_weight,
        requiredFor = listOf(MeasureMethod.FROM_SCALE, MeasureMethod.WEIGHT_ONLY),
        maxScale = 149,
        inputType = InputType.DOUBLE,
        unitType = UnitType.WEIGHT
    ),
    CHEST(
        titleRes = R.string.measure_chest,
        requiredFor = listOf(
            MeasureMethod.JACKSON_POLLOCK_7, MeasureMethod.JACKSON_POLLOCK_3, MeasureMethod.PARRILLO
        ),
        maxScale = 30,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    ABDOMINAL(
        titleRes = R.string.measure_abdominal,
        requiredFor = listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.JACKSON_POLLOCK_3,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        maxScale = 40,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    THIGH(
        titleRes = R.string.measure_thigh,
        requiredFor = listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.JACKSON_POLLOCK_3,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        maxScale = 40,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    TRICEP(
        titleRes = R.string.measure_tricep,
        requiredFor = listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.DURNIN_WOMERSLEY,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        maxScale = 30,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    SUBSCAPULAR(
        titleRes = R.string.measure_subscapular,
        requiredFor = listOf(
            MeasureMethod.JACKSON_POLLOCK_7, MeasureMethod.DURNIN_WOMERSLEY, MeasureMethod.PARRILLO
        ),
        maxScale = 40,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    SUPRAILIAC(
        titleRes = R.string.measure_suprailiac,
        requiredFor = listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.DURNIN_WOMERSLEY,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        maxScale = 40,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    MIDAXILARITY(
        titleRes = R.string.measure_midaxillary,
        requiredFor = listOf(MeasureMethod.JACKSON_POLLOCK_7),
        maxScale = 30,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    BICEP(
        titleRes = R.string.measure_bicep,
        requiredFor = listOf(MeasureMethod.DURNIN_WOMERSLEY, MeasureMethod.PARRILLO),
        maxScale = 30,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    LOWER_BACK(
        titleRes = R.string.measure_lower_back,
        requiredFor = listOf(MeasureMethod.PARRILLO),
        maxScale = 40,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    CALF(
        titleRes = R.string.measure_calf,
        requiredFor = listOf(MeasureMethod.PARRILLO),
        maxScale = 30,
        inputType = InputType.INT,
        unitType = UnitType.WIDTH
    ),
    BODY_FAT(
        titleRes = R.string.measure_fat,
        requiredFor = listOf(MeasureMethod.FROM_SCALE),
        maxScale = 49,
        inputType = InputType.DOUBLE,
        unitType = UnitType.PERCENT
    );

    enum class InputType {
        INT, DOUBLE
    }

    enum class UnitType {
        PERCENT, WEIGHT, WIDTH
    }
}
