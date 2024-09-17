package com.nmarsollier.fitfat.models.userSettings.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nmarsollier.fitfat.common.utils.deserialize
import com.nmarsollier.fitfat.common.utils.serializedName
import java.util.Date

private const val DATABASE_NAME = "userSettings"

@Database(entities = [UserSettingsEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class UserSettingsDatabase : RoomDatabase() {
    abstract fun userDao(): UserSettingsDao
}

internal fun userSettingsDatabase(context: Context): UserSettingsDatabase {
    return Room.databaseBuilder(
        context,
        UserSettingsDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(
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
    ).build()
}

internal class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toDate(value: Long?) = value?.let { Date(it) }

        @TypeConverter
        @JvmStatic
        fun toLong(value: Date?) = value?.time

        @TypeConverter
        @JvmStatic
        fun toSexType(value: String?) = value.deserialize<SexType>()

        @TypeConverter
        @JvmStatic
        fun toSexType(value: SexType?) = value.serializedName

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: String?) = value.deserialize<MeasureType>()

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: MeasureType?) = value.toString()
    }
}
