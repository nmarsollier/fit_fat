package com.nmarsollier.fitfat.model.measures

import androidx.room.*

@Dao
abstract class MeasureDao {
    @Query("SELECT * FROM measures ORDER BY date DESC")
    abstract fun findAll(): List<Measure>

    @Query("SELECT * FROM measures ORDER BY date DESC LIMIT 1")
    abstract fun findLast(): Measure?

    @Query("SELECT * FROM measures WHERE cloud_sync = 0 ")
    abstract fun findUnSynced(): List<Measure>?

    @Query("SELECT * FROM measures WHERE uid=:id")
    abstract fun findById(id: String): Measure?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun internalInsert(measure: Measure)

    @Delete
    abstract fun delete(measure: Measure)

    @Update
    abstract fun update(measure: Measure)

    fun insert(measure: Measure) {
        measure.calculateFatPercent()
        internalInsert(measure)
    }
}
