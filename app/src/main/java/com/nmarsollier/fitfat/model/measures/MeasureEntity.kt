package com.nmarsollier.fitfat.model.measures

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.SexType
import com.nmarsollier.fitfat.ui.common.displayWeight
import com.nmarsollier.fitfat.utils.toPounds
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID
import kotlin.math.log10

@Parcelize
@Entity(tableName = "measures")
data class Measure(
    @PrimaryKey val uid: String,

    @ColumnInfo(name = "body_weight") var bodyWeight: Double,

    @ColumnInfo(name = "body_height") var bodyHeight: Double,

    @ColumnInfo(name = "age") var age: Int,

    @ColumnInfo(name = "sex") var sex: SexType,

    @ColumnInfo(name = "date") var date: Date = Date(),

    @ColumnInfo(name = "measure_method") var measureMethod: MeasureMethod = MeasureMethod.DURNIN_WOMERSLEY,

    @ColumnInfo(name = "chest") var chest: Int = 0,

    @ColumnInfo(name = "abdominal") var abdominal: Int = 0,

    @ColumnInfo(name = "thigh") var thigh: Int = 0,

    @ColumnInfo(name = "tricep") var tricep: Int = 0,

    @ColumnInfo(name = "subscapular") var subscapular: Int = 0,

    @ColumnInfo(name = "suprailiac") var suprailiac: Int = 0,

    @ColumnInfo(name = "midaxillary") var midaxillary: Int = 0,

    @ColumnInfo(name = "bicep") var bicep: Int = 0,

    @ColumnInfo(name = "lower_back") var lowerBack: Int = 0,

    @ColumnInfo(name = "calf") var calf: Int = 0,

    @ColumnInfo(name = "fat_percent") var fatPercent: Double = 0.0,

    @ColumnInfo(name = "cloud_sync") var cloudSync: Boolean = false

) : Parcelable {
    fun isValid(): Boolean =
        MeasureValue.values().map { it.hasValidValue }.firstOrNull { !it } ?: true

    private val MeasureValue.hasValidValue: Boolean
        get() {
            val value = when (this) {
                MeasureValue.BODY_WEIGHT -> bodyWeight
                MeasureValue.CHEST -> chest.toDouble()
                MeasureValue.ABDOMINAL -> abdominal.toDouble()
                MeasureValue.THIGH -> thigh.toDouble()
                MeasureValue.TRICEP -> tricep.toDouble()
                MeasureValue.SUBSCAPULAR -> subscapular.toDouble()
                MeasureValue.SUPRAILIAC -> suprailiac.toDouble()
                MeasureValue.MIDAXILARITY -> midaxillary.toDouble()
                MeasureValue.BICEP -> bicep.toDouble()
                MeasureValue.LOWER_BACK -> lowerBack.toDouble()
                MeasureValue.CALF -> calf.toDouble()
                MeasureValue.BODY_FAT -> fatPercent
            }

            return !this.isRequiredForMethod(measureMethod) || value > 0
        }

    fun calculateFatPercent() {
        if (!isValid()) {
            fatPercent = 0.0
            return
        }

        fatPercent = when (measureMethod) {
            MeasureMethod.JACKSON_POLLOCK_7 -> {
                val sum =
                    chest + abdominal + thigh + tricep + subscapular + suprailiac + midaxillary
                val density = if (sex == SexType.MALE) {
                    1.112 - (0.00043499 * sum) + (0.00000055 * sum * sum) - (0.00028826 * age)
                } else {
                    1.097 - (0.00046971 * sum) + (0.00000056 * sum * sum) - (0.00012828 * age)
                }
                495 / density - 450
            }

            MeasureMethod.JACKSON_POLLOCK_4 -> {
                val sum = abdominal + thigh + tricep + suprailiac

                if (sex == SexType.MALE) {
                    (0.29288 * sum) - (0.0005 * sum * sum) + (0.15845 * age) - 5.76377
                } else {
                    (0.29669 * sum) - (0.00043 * sum * sum) + (0.02963 * age) - 1.4072
                }
            }

            MeasureMethod.JACKSON_POLLOCK_3 -> {
                val sum = abdominal + thigh + chest
                val density = if (sex == SexType.MALE) {
                    1.10938 - (0.0008267 * sum) + (0.0000016 * sum * sum) - (0.0002574 * age)
                } else {
                    1.0994291 - (0.0009929 * sum) + (0.0000023 * sum * sum) - (0.0001392 * age)
                }
                495 / density - 450
            }

            MeasureMethod.DURNIN_WOMERSLEY -> {
                val logSum = log10((bicep + tricep + subscapular + suprailiac).toDouble())

                val density = when (sex) {
                    SexType.MALE -> {
                        when (age) {
                            in 0..16 -> 1.1533 - (0.0643 * logSum)
                            in 17..19 -> 1.1620 - (0.0630 * logSum)
                            in 20..29 -> 1.1631 - (0.0632 * logSum)
                            in 30..39 -> 1.1422 - (0.0544 * logSum)
                            in 40..49 -> 1.1620 - (0.0700 * logSum)
                            else -> 1.1715 - (0.0779 * logSum)
                        }
                    }

                    SexType.FEMALE -> {
                        when (age) {
                            in 0..17 -> 1.1369 - (0.0598 * logSum)
                            in 18..19 -> 1.1549 - (0.0678 * logSum)
                            in 20..29 -> 1.1599 - (0.0717 * logSum)
                            in 30..39 -> 1.1423 - (0.0632 * logSum)
                            in 40..49 -> 1.1333 - (0.0612 * logSum)
                            else -> 1.1339 - (0.0645 * logSum)
                        }
                    }
                }
                495 / density - 450
            }

            MeasureMethod.PARRILLO -> {
                val sum = chest + abdominal + thigh + bicep + tricep + subscapular +
                        suprailiac + lowerBack + calf
                27 * sum / bodyWeight.toPounds()
            }

            MeasureMethod.FROM_SCALE -> fatPercent
            MeasureMethod.WEIGHT_ONLY -> 0.0
        }
    }

    fun getValueForMethod(
        measureValue: MeasureValue,
        userSettingsEntity: UserSettingsEntity? = null
    ): Number =
        when (measureValue) {
            MeasureValue.CHEST -> chest
            MeasureValue.ABDOMINAL -> abdominal
            MeasureValue.THIGH -> thigh
            MeasureValue.TRICEP -> tricep
            MeasureValue.SUBSCAPULAR -> subscapular
            MeasureValue.SUPRAILIAC -> suprailiac
            MeasureValue.MIDAXILARITY -> midaxillary
            MeasureValue.BICEP -> bicep
            MeasureValue.LOWER_BACK -> lowerBack
            MeasureValue.CALF -> calf
            MeasureValue.BODY_WEIGHT -> userSettingsEntity.displayWeight(bodyWeight)
            MeasureValue.BODY_FAT -> fatPercent
        }

    fun setValueForMethod(measureValue: MeasureValue, value: Number) {
        when (measureValue) {
            MeasureValue.CHEST -> chest = value.toInt()
            MeasureValue.ABDOMINAL -> abdominal = value.toInt()
            MeasureValue.THIGH -> thigh = value.toInt()
            MeasureValue.TRICEP -> tricep = value.toInt()
            MeasureValue.SUBSCAPULAR -> subscapular = value.toInt()
            MeasureValue.SUPRAILIAC -> suprailiac = value.toInt()
            MeasureValue.MIDAXILARITY -> midaxillary = value.toInt()
            MeasureValue.BICEP -> bicep = value.toInt()
            MeasureValue.LOWER_BACK -> lowerBack = value.toInt()
            MeasureValue.CALF -> calf = value.toInt()
            MeasureValue.BODY_WEIGHT -> bodyWeight = value.toDouble()
            MeasureValue.BODY_FAT -> fatPercent = value.toDouble()
        }
        calculateFatPercent()
    }

    val bodyFatMass: Double
        get() = if (bodyWeight > 0 && fatPercent > 0) {
            bodyWeight * (fatPercent / 100)
        } else {
            0.0
        }

    val leanWeight: Double
        get() = if (bodyWeight > 0 && fatPercent > 0) {
            bodyWeight * (1 - (fatPercent / 100))
        } else {
            0.0
        }

    val freeFatMassIndex: Double
        get() {
            val lw = leanWeight
            val bh = bodyHeight
            return if (lw > 0 && bh > 0) {
                lw / ((bh / 100) * (bh / 100))
            } else {
                0.0
            }
        }

    companion object {
        fun newMeasure(uid: String = UUID.randomUUID().toString()) =
            Measure(uid, 0.0, 0.0, 0, SexType.MALE)
    }
}

enum class MeasureMethod {
    JACKSON_POLLOCK_7, JACKSON_POLLOCK_3, JACKSON_POLLOCK_4,
    PARRILLO, DURNIN_WOMERSLEY, FROM_SCALE, WEIGHT_ONLY;
}

enum class MeasureValue(
    val titleRes: Int,
    private val requiredFor: List<MeasureMethod>,
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

    fun isRequiredForMethod(method: MeasureMethod): Boolean {
        return requiredFor.contains(method)
    }

    enum class InputType {
        INT, DOUBLE
    }

    enum class UnitType {
        PERCENT, WEIGHT, WIDTH
    }
}
