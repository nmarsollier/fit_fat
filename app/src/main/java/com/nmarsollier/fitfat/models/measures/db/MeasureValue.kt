package com.nmarsollier.fitfat.models.measures.db

import com.nmarsollier.fitfat.R

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
