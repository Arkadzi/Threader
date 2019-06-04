package me.arkadzi.threader.presentation.presenter

import android.support.annotation.StringRes
import me.arkadzi.threader.presentation.utils.MessageHandler
import me.arkadzi.threader.presentation.view.View


abstract class BasePresenter<V: View>(val messageHandler: MessageHandler) : Presenter<V> {
    protected var view: V? = null

    override fun onCreate(view: V) {
        this.view = view
    }

    override fun releaseView() {
        view = null
    }

    override fun showMessage(throwable: Throwable) {
        view?.showMessage(throwable.toString())
    }

    override fun showMessage(@StringRes resId: Int) {
        view?.showMessage(messageHandler.getMessage(resId))
    }
}