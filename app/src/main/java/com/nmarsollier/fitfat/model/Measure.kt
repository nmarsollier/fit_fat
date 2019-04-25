package com.nmarsollier.fitfat.model

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.toPounds
import java.util.*

@Dao
abstract class MeasureDao {
    @Query("SELECT * FROM measures ORDER BY date DESC")
    abstract fun getMeasures(): LiveData<List<Measure>>

    @Query("SELECT * FROM measures ORDER BY date DESC LIMIT 1")
    abstract fun getLastMeasure(): Measure?

    @Insert
    abstract fun internalInsert(measure: Measure)

    @Delete
    abstract fun delete(measure: Measure)

    fun insert(measure: Measure) {
        measure.calculateFatPercent()
        internalInsert(measure)
    }
}

@Entity(tableName = "measures")
data class Measure(
    @PrimaryKey
    val uid: String,

    @ColumnInfo(name = "body_weight")
    var bodyWeight: Double,

    @ColumnInfo(name = "age")
    var age: Int,

    @ColumnInfo(name = "sex")
    var sex: SexType

) {
    @ColumnInfo(name = "date")
    var date: Date = Date()

    @ColumnInfo(name = "measure_method")
    var measureMethod: MeasureMethod = MeasureMethod.DURNIN_WOMERSLEY

    @ColumnInfo(name = "chest")
    var chest: Int = 0

    @ColumnInfo(name = "abdominal")
    var abdominal: Int = 0

    @ColumnInfo(name = "thigh")
    var thigh: Int = 0

    @ColumnInfo(name = "tricep")
    var tricep: Int = 0

    @ColumnInfo(name = "subscapular")
    var subscapular: Int = 0

    @ColumnInfo(name = "suprailiac")
    var suprailiac: Int = 0

    @ColumnInfo(name = "midaxillary")
    var midaxillary: Int = 0

    @ColumnInfo(name = "bicep")
    var bicep: Int = 0

    @ColumnInfo(name = "lower_back")
    var lowerBack: Int = 0

    @ColumnInfo(name = "calf")
    var calf: Int = 0

    @ColumnInfo(name = "fat_percent")
    var fatPercent: Double = 0.0

    fun isEmpty(): Boolean {
        return (chest + abdominal + thigh + tricep + subscapular + suprailiac + midaxillary + bicep + lowerBack + calf) == 0
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


enum class MeasureValue(val titleRes: Int, private val requiredFor: List<MeasureMethod>) {
    BODY_WEIGHT(R.string.measure_weight, listOf(MeasureMethod.FROM_SCALE, MeasureMethod.WEIGHT_ONLY)),
    CHEST(
        R.string.measure_chest,
        listOf(MeasureMethod.JACKSON_POLLOCK_7, MeasureMethod.JACKSON_POLLOCK_3, MeasureMethod.PARRILLO)
    ),
    ABDOMINAL(
        R.string.measure_abdominal,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.JACKSON_POLLOCK_3,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        )
    ),
    THIGH(
        R.string.measure_thigh,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.JACKSON_POLLOCK_3,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        )
    ),
    TRICEP(
        R.string.measure_tricep,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.DURNIN_WOMERSLEY,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        )
    ),
    SUBSCAPULAR(
        R.string.measure_subscapular,
        listOf(MeasureMethod.JACKSON_POLLOCK_7, MeasureMethod.DURNIN_WOMERSLEY, MeasureMethod.PARRILLO)
    ),
    SUPRAILIAC(
        R.string.measure_suprailiac,
        listOf(
            MeasureMethod.JACKSON_POLLOCK_7,
            MeasureMethod.DURNIN_WOMERSLEY,
            MeasureMethod.JACKSON_POLLOCK_4,
            MeasureMethod.PARRILLO
        )
    ),
    MIDAXILARITY(R.string.measure_midaxillary, listOf(MeasureMethod.JACKSON_POLLOCK_7)),
    BICEP(R.string.measure_bicep, listOf(MeasureMethod.DURNIN_WOMERSLEY, MeasureMethod.PARRILLO)),
    LOWER_BACK(R.string.measure_lower_back, listOf(MeasureMethod.PARRILLO)),
    CALF(R.string.measure_calf, listOf(MeasureMethod.PARRILLO)),
    BODY_FAT(R.string.measure_fat, listOf(MeasureMethod.FROM_SCALE));

    fun getHolderType(): Int {
        return if (this == BODY_FAT) 2 else 1
    }

    fun isRequired(method: MeasureMethod): Boolean {
        return requiredFor.contains(method)
    }
}
