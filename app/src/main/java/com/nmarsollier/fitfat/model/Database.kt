package com.nmarsollier.fitfat.model

import android.content.Context
import androidx.room.*
import java.util.*

private var INSTANCE: FitFatDatabase? = null
private const val DATABASE_NAME = "fitfat"

@Database(entities = [UserSettings::class, Measure::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FitFatDatabase : RoomDatabase() {
    abstract fun userDao(): UserSettingsDao
    abstract fun measureDao(): MeasureDao
}

fun getRoomDatabase(context: Context): FitFatDatabase {
    return INSTANCE ?: Room.databaseBuilder(context, FitFatDatabase::class.java, DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build().also {
            INSTANCE = it
        }
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
        fun toSexType(value: String?) = if (value == null) null else SexType.valueOf(value)

        @TypeConverter
        @JvmStatic
        fun toSexType(value: SexType?) = value.toString()

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: String?) = if (value == null) null else MeasureType.valueOf(value)

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: MeasureType?) = value.toString()

        @TypeConverter
        @JvmStatic
        fun toMeasureMethod(value: String?) = if (value == null) null else MeasureMethod.valueOf(value)

        @TypeConverter
        @JvmStatic
        fun toMeasureMethod(value: MeasureMethod?) = value.toString()
    }
}
