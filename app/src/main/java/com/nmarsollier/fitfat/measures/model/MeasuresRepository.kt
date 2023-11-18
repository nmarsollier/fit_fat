package com.nmarsollier.fitfat.measures.model

import com.nmarsollier.fitfat.measures.model.db.MeasureDao
import kotlinx.coroutines.coroutineScope

class MeasuresRepository internal constructor(
    private val dao: MeasureDao
) {
    suspend fun findById(id: String) = coroutineScope {
        dao.findById(id)?.asMeasure
    }

    suspend fun findAll() = coroutineScope {
        dao.findAll().map { it.asMeasure }
    }

    suspend fun findLast() = coroutineScope {
        dao.findLast()?.asMeasure
    }

    suspend fun update(measure: Measure) = coroutineScope {
        measure.recalculateFatPercent()

        dao.insert(
            measure.value
        )
    }

    suspend fun findUnSynced(): List<Measure>? = coroutineScope {
        dao.findUnSynced()?.map { it.asMeasure }
    }

    suspend fun delete(measure: Measure) = coroutineScope {
        dao.delete(measure.value)
    }
}

