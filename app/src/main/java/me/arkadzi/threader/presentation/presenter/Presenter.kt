package me.arkadzi.threader.presentation.presenter

import android.support.annotation.StringRes
import me.arkadzi.threader.presentation.view.View

interface Presenter<V: View> {
    fun onCreate(view: V)
    fun releaseView()
    fun showMessage(throwable: Throwable)
    fun showMessage(@StringRes resId: Int)
}