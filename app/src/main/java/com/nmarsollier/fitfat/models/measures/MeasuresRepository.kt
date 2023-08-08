package com.nmarsollier.fitfat.models.measures

import com.nmarsollier.fitfat.models.common.RepositoryUpdate
import com.nmarsollier.fitfat.models.common.StateRepository
import com.nmarsollier.fitfat.models.measures.db.MeasureDao
import com.nmarsollier.fitfat.models.measures.db.MeasureEntity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MeasuresRepository internal constructor(
    private val dao: MeasureDao
) : StateRepository() {
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

        launch {
            dao.insert(measure.asMeasureEntity)
        }.join()

        RepositoryUpdate.Save.sendToEvent()
    }

    suspend fun findUnSynced(): List<Measure>? = coroutineScope {
        dao.findUnSynced()?.map { it.asMeasure }
    }

    suspend fun delete(measure: Measure) = coroutineScope {
        launch {
            dao.delete(measure.asMeasureEntity)
        }.join()
        RepositoryUpdate.Delete.sendToEvent()
    }
}

val Measure.asMeasureEntity
    get() = MeasureEntity(
        uid = uid,
        date = date,
        bodyHeight = bodyHeight,
        age = age,
        sex = sex,
        measureMethod = measureMethod,
        bodyWeight = bodyWeight,
        chest = chest,
        abdominal = abdominal,
        thigh = thigh,
        tricep = tricep,
        subscapular = subscapular,
        suprailiac = suprailiac,
        midaxillary = midaxillary,
        bicep = bicep,
        lowerBack = lowerBack,
        calf = calf,
        fatPercent = fatPercent,
        cloudSync = cloudSync,
    )
