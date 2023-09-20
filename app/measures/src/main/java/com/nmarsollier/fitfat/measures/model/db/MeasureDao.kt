package com.nmarsollier.fitfat.measures.model.db

import androidx.room.*

@Dao
internal abstract class MeasureDao {
    @Query("SELECT * FROM measures ORDER BY date DESC")
    abstract fun findAll(): List<MeasureData>

    @Query("SELECT * FROM measures ORDER BY date DESC LIMIT 1")
    abstract fun findLast(): MeasureData?

    @Query("SELECT * FROM measures WHERE cloud_sync = 0 ")
    abstract fun findUnSynced(): List<MeasureData>?

    @Query("SELECT * FROM measures WHERE uid=:id")
    abstract fun findById(id: String): MeasureData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(measure: MeasureData)

    @Delete
    abstract fun delete(measure: MeasureData)
}
