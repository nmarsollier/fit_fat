package com.nmarsollier.fitfat.model.measures

import com.nmarsollier.fitfat.model.db.FitFatDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MeasuresRepository(
    private val database: FitFatDatabase, private val firebaseRepository: FirebaseRepository
) {
    suspend fun loadAll() = coroutineScope {
        database.measureDao().findAll()
    }

    suspend fun findLast() = coroutineScope {
        database.measureDao().findLast()
    }

    fun update(measure: Measure) {
        measure.cloudSync = false
        database.measureDao().update(measure)
    }

    suspend fun findUnsynced(): List<Measure>? = coroutineScope {
        database.measureDao().findUnsynced()
    }

    private suspend fun findById(id: String) = coroutineScope {
        database.measureDao().findById(id)
    }

    fun delete(
        measure: Measure
    ) {
        database.measureDao().delete(measure)
        firebaseRepository.deleteMeasure(measure)
    }

    suspend fun updateFromFirebase(document: Measure) = coroutineScope {
        async {
            database.measureDao().insert(document)
        }
    }

    fun insert(measure: Measure) = MainScope().launch(Dispatchers.IO) {
        database.measureDao().insert(measure)
    }
}