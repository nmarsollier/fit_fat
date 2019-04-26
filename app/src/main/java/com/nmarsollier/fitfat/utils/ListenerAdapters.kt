package com.nmarsollier.fitfat.utils

import android.widget.SeekBar

fun onProgressChangeListener(onProgressChange: (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit): SeekBar.OnSeekBarChangeListener {
    return object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChange.invoke(seekBar, progress, fromUser)
        }
    }
}
