package com.nmarsollier.fitfat.ui.measuresList

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nmarsollier.fitfat.BuildConfig
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.logger

@Composable
fun MeasuresListMenu() {
    val context = LocalContext.current as? Activity

    TopAppBar(title = { Text(stringResource(R.string.home_measure_title)) }, actions = {
        if (BuildConfig.DEBUG) {
            IconButton(onClick = {
                context?.let {
                    openDbInspector(it)
                }
            }) {
                Icon(Icons.Default.Search, stringResource(id = R.string.save_dialog_title))
            }
        }
    })
}

fun openDbInspector(activity: Activity) {
    try {
        val intent = Intent()
        intent.setClassName(
            activity.packageName, "im.dino.dbinspector.activities.DbInspectorActivity"
        )
        activity.startActivity(intent)
    } catch (e: Exception) {
        logger.severe("Unable to launch db inspector $e")
    }
}

@Preview
@Composable
fun MeasuresListMenuPreview() {
    KoinPreview {
        Column {
            MeasuresListMenu()
        }
    }
}
