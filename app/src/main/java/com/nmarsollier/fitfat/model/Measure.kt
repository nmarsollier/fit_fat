package com.nmarsollier.fitfat.model

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.getAge
import com.nmarsollier.fitfat.utils.toPounds
import kotlinx.android.parcel.Parcelize
import java.util.*

@Dao
abstract class MeasureDao {
    @Query("SELECT * FROM measures ORDER BY date DESC")
    abstract fun findAll(): LiveData<List<Measure>>

    @Query("SELECT * FROM measures ORDER BY date DESC LIMIT 1")
    abstract fun findLast(): Measure?

    @Query("SELECT * FROM measures WHERE cloud_sync = 0 ")
    abstract fun findUnsynced(): List<Measure>?

    @Query("SELECT * FROM measures WHERE uid=:id")
    abstract fun findById(id: String): Measure?

    @Insert
    protected abstract fun internalInsert(measure: Measure)

    @Delete
    abstract fun delete(measure: Measure)

    @Update
    abstract fun update(measure: Measure)

    fun insert(measure: Measure) {
        measure.calculateFatPercent()
        internalInsert(measure)
    }
}

@Parcelize
@Entity(tableName = "measures")
data class Measure(
    @PrimaryKey
    val uid: String,

    @ColumnInfo(name = "body_weight")
    var bodyWeight: Double,

    @ColumnInfo(name = "age")
    var age: Int,

    @ColumnInfo(name = "sex")
    var sex: SexType,

    @ColumnInfo(name = "date")
    var date: Date = Date(),

    @ColumnInfo(name = "measure_method")
    var measureMethod: MeasureMethod = MeasureMethod.DURNIN_WOMERSLEY,

    @ColumnInfo(name = "chest")
    var chest: Int = 0,

    @ColumnInfo(name = "abdominal")
    var abdominal: Int = 0,

    @ColumnInfo(name = "thigh")
    var thigh: Int = 0,

    @ColumnInfo(name = "tricep")
    var tricep: Int = 0,

    @ColumnInfo(name = "subscapular")
    var subscapular: Int = 0,

    @ColumnInfo(name = "suprailiac")
    var suprailiac: Int = 0,

    @ColumnInfo(name = "midaxillary")
    var midaxillary: Int = 0,

    @ColumnInfo(name = "bicep")
    var bicep: Int = 0,

    @ColumnInfo(name = "lower_back")
    var lowerBack: Int = 0,

    @ColumnInfo(name = "calf")
    var calf: Int = 0,

    @ColumnInfo(name = "fat_percent")
    var fatPercent: Double = 0.0,

    @ColumnInfo(name = "cloud_sync")
    var cloudSync: Boolean = false

) : Parcelable {
    fun isEmpty(): Boolean {
        return (chest + abdominal + thigh + tricep + subscapular + suprailiac + midaxillary + bicep + lowerBack + calf + fatPercent + bodyWeight) == 0.0
    }

    fun isValid(): Boolean {
        return (!MeasureValue.CALF.isRequired(measureMethod) || calf > 0)
                && (!MeasureValue.CHEST.isRequired(measureMethod) || chest > 0)
                && (!MeasureValue.ABDOMINAL.isRequired(measureMethod) || abdominal > 0)
                && (!MeasureValue.THIGH.isRequired(measureMethod) || thigh > 0)
                && (!MeasureValue.TRICEP.isRequired(measureMethod) || tricep > 0)
                && (!MeasureValue.SUBSCAPULAR.isRequired(measureMethod) || subscapular > 0)
                && (!MeasureValue.SUPRAILIAC.isRequired(measureMethod) || suprailiac > 0)
                && (!MeasureValue.MIDAXILARITY.isRequired(measureMethod) || midaxillary > 0)
                && (!MeasureValue.BICEP.isRequired(measureMethod) || bicep > 0)
                && (!MeasureValue.LOWER_BACK.isRequired(measureMethod) || lowerBack > 0)
                && (!MeasureValue.BODY_FAT.isRequired(measureMethod) || fatPercent > 0)
                && (!MeasureValue.BODY_WEIGHT.isRequired(measureMethod) || bodyWeight > 0)
    }

    fun calculateFatPercent() {
        if (!isValid()) {
            fatPercent = 0.0
            return
        }

        fatPercent = when (measureMethod) {
            MeasureMethod.JACKSON_POLLOCK_7 -> {
                val sum = chest + abdominal + thigh + tricep + subscapular + suprailiac + midaxillary
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
                val logSum = Math.log10((bicep + tricep + subscapular + suprailiac).toDouble())

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
                val sum = chest + abdominal + thigh + bicep + tricep + subscapular + suprailiac + lowerBack + calf
                27 * sum / bodyWeight.toPounds()
            }
            MeasureMethod.FROM_SCALE -> {
                fatPercent
            }
            MeasureMethod.WEIGHT_ONLY -> {
                0.0
            }
        }
    }

    fun getValueForMethod(measureValue: MeasureValue): Number {
        return when (measureValue) {
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
            MeasureValue.BODY_WEIGHT -> bodyWeight
            MeasureValue.BODY_FAT -> fatPercent
        }
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
    }

    companion object {
        fun newMeasure() = Measure(UUID.randomUUID().toString(), 0.0, 0, SexType.MALE)

        fun newMeasure(uid: String) = Measure(uid, 0.0, 0, SexType.MALE)

        fun newMeasure(userSettings: UserSettings): Measure {
            return Measure(
                UUID.randomUUID().toString(),
                userSettings.weight,
                userSettings.birthDate.getAge(),
                userSettings.sex
            )
        }
    }
}

enum class MeasureMethod(val labelRes: Int) {
    JACKSON_POLLOCK_7(R.string.measure_method_jackson_pollock_7),
    JACKSON_POLLOCK_3(R.string.measure_method_jackson_pollock_3),
    JACKSON_POLLOCK_4(R.string.measure_method_jackson_pollock_4),
    PARRILLO(R.string.measure_method_parrillo),
    DURNIN_WOMERSLEY(R.string.measure_method_durnin_womersley),
    FROM_SCALE(R.string.measure_method_manual_scale),
    WEIGHT_ONLY(R.string.measure_method_weight);
}

enum class InputType {
    INT, DOUBLE
}

enum class UnitType {
    PERCENT, WEIGHT, WIDTH
}

enum class MeasureValue(
    val titleRes: Int,
    val helpRes: Int?,
    val colorRes: Int,
    private val requiredFor: List<MeasureMethod>,
    val maxScale: Int,
    val inputType: InputType,
    val unitType: UnitType
) {
    BODY_WEIGHT(
        R.string.measure_weight,
        null,
        R.color.chartBodyWeight,
        listOf(MeasureMethod.FROM_SCALE, MeasureMethod.WEIGHT_ONLY),
        200,
        InputType.DOUBLE,
        UnitType.WEIGHT
    ),
    CHEST(
        R.string.measure_chest,
        R.drawable.img_chest,
        R.color.chartChest,
        listOf(MeasureMethod.JACKSON_POLLOCK_7, MeasureMethod.JACKSON_POLLOCK_3, MeasureMethod.PARRILLO),
        30,
        InputType.INT,
        UnitType.WIDTH
    ),
    ABDOMINAL(
        R.string.measure_abdominal,
        R.drawable.img_abdominal,
        R.color.chartAbdominal,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.JACKSON_POLLOCK_3,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        60,
        InputType.INT,
        UnitType.WIDTH
    ),
    THIGH(
        R.string.measure_thigh,
        R.drawable.img_thigh,
        R.color.chartThigh,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.JACKSON_POLLOCK_3,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        30,
        InputType.INT,
        UnitType.WIDTH
    ),
    TRICEP(
        R.string.measure_tricep,
        R.drawable.img_tricep,
        R.color.chartTricep,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.DURNIN_WOMERSLEY,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        30,
        InputType.INT,
        UnitType.WIDTH
    ),
    SUBSCAPULAR(
        R.string.measure_subscapular,
        R.drawable.img_subscapular,
        R.color.chartSubscapular,
        listOf(MeasureMethod.JACKSON_POLLOCK_7, MeasureMethod.DURNIN_WOMERSLEY, MeasureMethod.PARRILLO),
        60,
        InputType.INT,
        UnitType.WIDTH
    ),
    SUPRAILIAC(
        R.string.measure_suprailiac,
        R.drawable.img_suprailiac,
        R.color.chartSuprailiac,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.DURNIN_WOMERSLEY,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        ),
        60,
        InputType.INT,
        UnitType.WIDTH
    ),
    MIDAXILARITY(
        R.string.measure_midaxillary,
        R.drawable.img_midaxilarity,
        R.color.chartMidaxilarity,
        listOf(MeasureMethod.JACKSON_POLLOCK_7),
        30,
        InputType.INT,
        UnitType.WIDTH
    ),
    BICEP(
        R.string.measure_bicep,
        R.drawable.img_bicep,
        R.color.chartBicep,
        listOf(MeasureMethod.DURNIN_WOMERSLEY, MeasureMethod.PARRILLO),
        30,
        InputType.INT,
        UnitType.WIDTH
    ),
    LOWER_BACK(
        R.string.measure_lower_back,
        R.drawable.img_lower_back,
        R.color.chartLowerBack,
        listOf(MeasureMethod.PARRILLO),
        60,
        InputType.INT,
        UnitType.WIDTH
    ),
    CALF(
        R.string.measure_calf,
        R.drawable.img_calf,
        R.color.chartCalf,
        listOf(MeasureMethod.PARRILLO),
        30,
        InputType.INT,
        UnitType.WIDTH
    ),
    BODY_FAT(
        R.string.measure_fat,
        null,
        R.color.chartBodyFat,
        listOf(MeasureMethod.FROM_SCALE),
        99,
        InputType.DOUBLE,
        UnitType.PERCENT
    );

    fun isRequired(method: MeasureMethod): Boolean {
        return requiredFor.contains(method)
    }
}
