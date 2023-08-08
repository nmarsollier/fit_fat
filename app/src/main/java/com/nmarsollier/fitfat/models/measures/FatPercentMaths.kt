package com.nmarsollier.fitfat.models.measures

import com.nmarsollier.fitfat.utils.toPounds
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.models.userSettings.db.SexType
import kotlin.math.log10

internal fun MeasureMethod.fatPercent(measure: Measure): Double {
    fun jacksonPollock7(measure: Measure): Double {
        val sum =
            measure.chest + measure.abdominal + measure.thigh +
                    measure.tricep + measure.subscapular +
                    measure.suprailiac + measure.midaxillary
        val density = if (measure.sex == SexType.MALE) {
            1.112 - (0.00043499 * sum) + (0.00000055 * sum * sum) - (0.00028826 * measure.age)
        } else {
            1.097 - (0.00046971 * sum) + (0.00000056 * sum * sum) - (0.00012828 * measure.age)
        }
        return 495 / density - 450
    }

    fun jacksonPollock3(measure: Measure): Double {
        val sum = measure.abdominal + measure.thigh + measure.chest
        val density =
            if (measure.sex == SexType.MALE) {
                1.10938 - (0.0008267 * sum) + (0.0000016 * sum * sum) - (0.0002574 * measure.age)
            } else {
                1.0994291 - (0.0009929 * sum) + (0.0000023 * sum * sum) - (0.0001392 * measure.age)
            }
        return 495 / density - 450
    }

    fun jacksonPollock4(measure: Measure): Double {
        val sum =
            measure.abdominal + measure.thigh + measure.tricep + measure.suprailiac

        return if (measure.sex == SexType.MALE) {
            (0.29288 * sum) - (0.0005 * sum * sum) + (0.15845 * measure.age) - 5.76377
        } else {
            (0.29669 * sum) - (0.00043 * sum * sum) + (0.02963 * measure.age) - 1.4072
        }
    }

    fun parrillo(measure: Measure): Double {
        val sum = measure.chest + measure.abdominal + measure.thigh +
                measure.bicep + measure.tricep + measure.subscapular +
                measure.suprailiac + measure.lowerBack + measure.calf
        return 27 * sum / measure.bodyWeight.toPounds
    }

    fun durninWomersley(measure: Measure): Double {
        val logSum =
            log10((measure.bicep + measure.tricep + measure.subscapular + measure.suprailiac).toDouble())

        val density = when (measure.sex) {
            SexType.MALE -> {
                when (measure.age) {
                    in 0..16 -> 1.1533 - (0.0643 * logSum)
                    in 17..19 -> 1.1620 - (0.0630 * logSum)
                    in 20..29 -> 1.1631 - (0.0632 * logSum)
                    in 30..39 -> 1.1422 - (0.0544 * logSum)
                    in 40..49 -> 1.1620 - (0.0700 * logSum)
                    else -> 1.1715 - (0.0779 * logSum)
                }
            }

            SexType.FEMALE -> {
                when (measure.age) {
                    in 0..17 -> 1.1369 - (0.0598 * logSum)
                    in 18..19 -> 1.1549 - (0.0678 * logSum)
                    in 20..29 -> 1.1599 - (0.0717 * logSum)
                    in 30..39 -> 1.1423 - (0.0632 * logSum)
                    in 40..49 -> 1.1333 - (0.0612 * logSum)
                    else -> 1.1339 - (0.0645 * logSum)
                }
            }
        }
        return 495 / density - 450
    }

    fun fromScale(measure: Measure): Double = measure.fatPercent

    fun weightOnly(): Double = 0.0

    return when (this) {
        MeasureMethod.JACKSON_POLLOCK_7 -> jacksonPollock7(measure)
        MeasureMethod.JACKSON_POLLOCK_3 -> jacksonPollock3(measure)
        MeasureMethod.JACKSON_POLLOCK_4 -> jacksonPollock4(measure)
        MeasureMethod.PARRILLO -> parrillo(measure)
        MeasureMethod.DURNIN_WOMERSLEY -> durninWomersley(measure)
        MeasureMethod.FROM_SCALE -> fromScale(measure)
        MeasureMethod.WEIGHT_ONLY -> weightOnly()
    }
}
