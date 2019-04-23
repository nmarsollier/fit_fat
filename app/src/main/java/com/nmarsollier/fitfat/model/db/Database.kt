package com.nmarsollier.fitfat.model.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureDao
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.userSettings.MeasureType
import com.nmarsollier.fitfat.model.userSettings.SexType
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsDao
import java.util.*

private var INSTANCE: FitFatDatabase? = null
private const val DATABASE_NAME = "fitfat"

@Database(entities = [UserSettings::class, Measure::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FitFatDatabase : RoomDatabase() {
    abstract fun userDao(): UserSettingsDao
    abstract fun measureDao(): MeasureDao
}

fun getRoomDatabase(context: Context): FitFatDatabase {
    return INSTANCE ?: Room.databaseBuilder(context, FitFatDatabase::class.java, DATABASE_NAME)
        .addMigrations(
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                }
            },
            object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE measures add column body_height REAL not null default 0")
                    database.execSQL("UPDATE measures set body_height = ifnull((SELECT MAX(height) from user_settings), 0) ")
                }
            }
        ).build().also {
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
        fun toMeasureMethod(value: String?) =
            if (value == null) null else MeasureMethod.valueOf(value)

        @TypeConverter
        @JvmStatic
        fun toMeasureMethod(value: MeasureMethod?) = value.toString()
    }
}
