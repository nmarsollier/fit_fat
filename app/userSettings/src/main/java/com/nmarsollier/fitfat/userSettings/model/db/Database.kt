package com.nmarsollier.fitfat.userSettings.model.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nmarsollier.fitfat.utils.converters.deserialize
import com.nmarsollier.fitfat.utils.converters.serializedName
import java.util.*

private const val DATABASE_NAME = "userSettings"

@Database(entities = [UserSettingsData::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class UserSettingsDatabase : RoomDatabase() {
    abstract fun userDao(): UserSettingsDao
}

internal fun getRoomDatabase(context: Context): UserSettingsDatabase {
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
        fun toSexType(value: String?) = value.deserialize<UserSettingsData.SexType>()

        @TypeConverter
        @JvmStatic
        fun toSexType(value: UserSettingsData.SexType?) = value.serializedName

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: String?) = value.deserialize<UserSettingsData.MeasureType>()

        @TypeConverter
        @JvmStatic
        fun toMeasureType(value: UserSettingsData.MeasureType?) = value.toString()
    }
}
