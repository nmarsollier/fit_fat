package com.nmarsollier.fitfat.model

import android.content.Context
import androidx.room.*
import java.util.*

@Database(entities = [UserSettings::class, Measure::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FitFatDatabase : RoomDatabase() {
    abstract fun userDao(): UserSettingsDao
    abstract fun measureDao(): MeasureDao
}

private var INSTANCE: FitFatDatabase? = null

fun getRoomDatabase(context: Context): FitFatDatabase {
    if (INSTANCE == null) {
        INSTANCE = Room.databaseBuilder(
            context,
            FitFatDatabase::class.java, "fitfat"
        ).build()
    }
    return INSTANCE!!
}

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toDate(value: Long?) = if (value == null) null else Date(value)

        @TypeConverter
        @JvmStatic
        fun toLong(value: Date?) = value?.time

        @TypeConverter
        @JvmStatic
        fun toSexType(value: Int?) = if (value == null) null else SexType.values()[value]

        @TypeConverter
        @JvmStatic
        fun toSexType(value: SexType?) = value?.ordinal

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: Int?) = if (value == null) null else MeasureType.values()[value]

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: MeasureType?) = value?.ordinal

        @TypeConverter
        @JvmStatic
        fun toMeasureMethod(value: Int?) = if (value == null) null else MeasureMethod.values()[value]

        @TypeConverter
        @JvmStatic
        fun toMeasureMethod(value: MeasureMethod?) = value?.ordinal
    }
}


