package me.arkadzi.threader.presentation.utils

import android.content.Context
import android.support.annotation.StringRes

class MessageHandler(val context: Context) {
    fun getMessage(@StringRes resId: Int) =
            context.getString(resId)
}