package com.nmarsollier.fitfat.models.measures.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nmarsollier.fitfat.models.userSettings.db.SexType
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
@Entity(tableName = "measures")
data class MeasureEntity(
    @PrimaryKey val uid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "body_weight") val bodyWeight: Double,
    @ColumnInfo(name = "body_height") val bodyHeight: Double,
    @ColumnInfo(name = "age") val age: Int = 0,
    @ColumnInfo(name = "sex") val sex: SexType,
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
