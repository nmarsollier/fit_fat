package com.nmarsollier.fitfat.model.measures

import com.nmarsollier.fitfat.model.db.FitFatDatabase
import kotlinx.coroutines.coroutineScope

class MeasuresRepository(
    private val database: FitFatDatabase
) {
    suspend fun loadAll() = coroutineScope {
        database.measureDao().findAll()
    }

    suspend fun findLast() = coroutineScope {
        database.measureDao().findLast()
    }

    suspend fun update(measure: Measure) = coroutineScope {
        measure.cloudSync = false
        database.measureDao().update(measure)
    }

    suspend fun findUnSynced(): List<Measure>? = coroutineScope {
        database.measureDao().findUnSynced()
    }

    suspend fun delete(measure: Measure) = coroutineScope {
        database.measureDao().delete(measure)
    }

    suspend fun insert(measure: Measure) = coroutineScope {
        database.measureDao().insert(measure)
    }
}