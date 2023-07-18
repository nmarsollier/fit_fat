package com.nmarsollier.fitfat.model.measures

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MeasureDao {
    @Query("SELECT * FROM measures ORDER BY date DESC")
    abstract fun findAll(): Flow<List<Measure>>

    @Query("SELECT * FROM measures ORDER BY date DESC LIMIT 1")
    abstract fun findLast(): Flow<Measure?>

    @Query("SELECT * FROM measures WHERE cloud_sync = 0 ")
    abstract fun findUnsynced(): Flow<List<Measure>?>

    @Query("SELECT * FROM measures WHERE uid=:id")
    abstract fun findById(id: String): Flow<Measure?>

    @Insert
    protected abstract fun internalInsert(measure: Measure)

    @Delete
    abstract fun delete(measure: Measure)

    @Update
    abstract fun update(measure: Measure)

    suspend fun insert(measure: Measure) {
        measure.calculateFatPercent()
        internalInsert(measure)
    }
}
