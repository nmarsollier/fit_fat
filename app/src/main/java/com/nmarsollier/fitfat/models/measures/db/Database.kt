package com.nmarsollier.fitfat.models.measures.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nmarsollier.fitfat.utils.deserialize
import com.nmarsollier.fitfat.utils.serializedName
import com.nmarsollier.fitfat.models.userSettings.db.SexType
import java.util.Date

private const val DATABASE_NAME = "measures"

@Database(entities = [MeasureEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class MeasuresDatabase : RoomDatabase() {
    abstract fun measureDao(): MeasureDao
}

internal fun measuresDatabase(context: Context): MeasuresDatabase {
    return Room.databaseBuilder(
        context,
        MeasuresDatabase::class.java,
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
        fun toMeasureMethod(value: String?) = value.deserialize<MeasureMethod>()

        @TypeConverter
        @JvmStatic
        fun toMeasureMethod(value: MeasureMethod?) = value.toString()
    }
}
