package com.nmarsollier.fitfat.model

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull

object MeasuresRepository {
    fun loadAll(context: Context): Flow<List<Measure>> = channelFlow {
        getRoomDatabase(context).measureDao().findAll().firstOrNull {
            send(it)
            true
        }
    }

    fun findLast(context: Context): Flow<Measure?> = channelFlow {
        getRoomDatabase(context).measureDao().findLast().firstOrNull {
            send(it)
            true
        }
    }

    fun delete(
        context: Context, measure: Measure
    ): Flow<Unit> = channelFlow {
        getRoomDatabase(context).measureDao().delete(measure)
        send(Unit)
    }
}