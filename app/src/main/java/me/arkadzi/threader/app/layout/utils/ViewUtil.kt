package me.arkadzi.threader.app.layout.utils

import android.widget.SeekBar
import android.widget.TextView
import me.arkadzi.threader.app.layout.listener.SimpleSeekbarListener

fun SeekBar.setProgressChangeListener(listener: (progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SimpleSeekbarListener() {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            listener(progress)
        }
    })
}

val TextView.textStr
    get() = text.toString()
