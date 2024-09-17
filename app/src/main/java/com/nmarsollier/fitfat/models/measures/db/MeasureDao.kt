package com.nmarsollier.fitfat.models.measures.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal abstract class MeasureDao {
    @Query("SELECT * FROM measures ORDER BY date DESC")
    abstract suspend fun findAll(): List<MeasureEntity>

    @Query("SELECT * FROM measures ORDER BY date DESC LIMIT 1")
    abstract suspend fun findLast(): MeasureEntity?

    @Query("SELECT * FROM measures WHERE cloud_sync = 0 ")
    abstract suspend fun findUnSynced(): List<MeasureEntity>?

    @Query("SELECT * FROM measures WHERE uid=:id")
    abstract suspend fun findById(id: String): MeasureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(measure: MeasureEntity)

    @Delete
    abstract suspend fun delete(measure: MeasureEntity)
}
