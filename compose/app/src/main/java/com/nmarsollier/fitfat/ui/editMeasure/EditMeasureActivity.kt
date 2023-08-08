package com.nmarsollier.fitfat.ui.editMeasure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.core.content.ContextCompat
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.utils.jsonToObject
import com.nmarsollier.fitfat.utils.toJson

private const val MEASURE = "measure"

class EditMeasureActivity : AppCompatActivity() {
    private val measure: Measure?
        get() = intent.getStringExtra(MEASURE).jsonToObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContent {
            MaterialTheme {
                EditMeasureScreen(measure)
            }
        }
    }

    companion object {
        fun startActivity(context: Context, measure: Measure) {
            val intent = Intent(context, EditMeasureActivity::class.java)
            intent.putExtra(MEASURE, measure.toJson())
            ContextCompat.startActivity(context, intent, null)
        }

        fun startActivity(context: Context) {
            val intent = Intent(context, EditMeasureActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }
}
