package com.nmarsollier.fitfat.measures.ui.list

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
import com.nmarsollier.fitfat.common.logger.Logger
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import org.koin.compose.koinInject

@Composable
fun MeasuresListMenu(
    logger: Logger = koinInject()
) {
    val context = LocalContext.current as? Activity

    TopAppBar(title = { Text(stringResource(R.string.home_measure_title)) }, actions = {
        if (BuildConfig.DEBUG) {
            IconButton(onClick = {
                context?.openDbInspector(logger)
            }) {
                Icon(Icons.Default.Search, stringResource(id = R.string.save_dialog_title))
            }
        }
    })
}

private fun Activity.openDbInspector(logger: Logger) {
    try {
        val intent = Intent()
        intent.setClassName(
            this.packageName, "im.dino.dbinspector.activities.DbInspectorActivity"
        )
        this.startActivity(intent)
    } catch (e: Exception) {
        logger.e("Unable to launch db inspector", e)
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
