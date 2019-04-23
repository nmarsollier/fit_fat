package com.nmarsollier.fitfat.model

import androidx.lifecycle.LiveData
import androidx.room.*
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
        return (!measureMethod.calfRequired() || calf > 0)
                && (!measureMethod.chestRequired() || chest > 0)
                && (!measureMethod.abdominalRequired() || abdominal > 0)
                && (!measureMethod.thighRequired() || thigh > 0)
                && (!measureMethod.tricepRequired() || tricep > 0)
                && (!measureMethod.subscapularRequired() || subscapular > 0)
                && (!measureMethod.suprailiacRequired() || suprailiac > 0)
                && (!measureMethod.midaxillaryRequired() || midaxillary > 0)
                && (!measureMethod.bicepRequired() || bicep > 0)
                && (!measureMethod.lowerBackRequired() || lowerBack > 0)
                && (!measureMethod.fatPercentRequired() || fatPercent > 0)
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
        }
    }
}

enum class MeasureMethod(private val label: String) {
    JACKSON_POLLOCK_7("Jackson/Pollock 7"),
    JACKSON_POLLOCK_3("Jackson/Pollock 3"),
    JACKSON_POLLOCK_4("Jackson/Pollock 4"),
    PARRILLO("Parrillo"),
    DURNIN_WOMERSLEY("Durnin/Womersley"),
    FROM_SCALE("Manual/Scale");

    override fun toString(): String {
        return label
    }

    fun chestRequired() = this == JACKSON_POLLOCK_7 || this == JACKSON_POLLOCK_3 || this == PARRILLO

    fun abdominalRequired() = this != DURNIN_WOMERSLEY && this != FROM_SCALE

    fun thighRequired() = this != DURNIN_WOMERSLEY && this != FROM_SCALE

    fun tricepRequired() = this != JACKSON_POLLOCK_3 && this != FROM_SCALE

    fun subscapularRequired() = this == JACKSON_POLLOCK_7 || this == PARRILLO || this == DURNIN_WOMERSLEY

    fun suprailiacRequired() = this != JACKSON_POLLOCK_3 && this != FROM_SCALE

    fun midaxillaryRequired() = this == JACKSON_POLLOCK_7

    fun bicepRequired() = this == PARRILLO || this == DURNIN_WOMERSLEY

    fun lowerBackRequired() = this == PARRILLO

    fun calfRequired() = this == PARRILLO

    fun fatPercentRequired() = this == FROM_SCALE
}

