package com.nmarsollier.fitfat.stats.ui.utils.preview

import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.stats.ui.stats.StatsReducer
import com.nmarsollier.fitfat.stats.ui.stats.StatsViewModel

interface StatsViewModelSamples {
    fun reducer(): StatsReducer
}

val StatsViewModel.Companion.Samples: StatsViewModelSamples
    get() = object : StatsViewModelSamples {
        override fun reducer() = object : StatsReducer {
            override fun init() = Unit
            override fun updateMethod(selectedMethod: MeasureMethod) =
                Unit

            override fun toggleShowMethod() = Unit
        }
    }
