package me.arkadzi.threader.presentation.features.main

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.arkadzi.threader.R
import me.arkadzi.threader.domain.model.Page
import me.arkadzi.threader.domain.processing.BfsPageAnalyzer
import me.arkadzi.threader.presentation.presenter.BasePresenter
import me.arkadzi.threader.presentation.utils.MessageHandler
import java.util.concurrent.TimeUnit

class MainPresenter(
        val analyzer: BfsPageAnalyzer,
        messageHandler: MessageHandler
) : BasePresenter<IMainView>(messageHandler), IMainPresenter {

    private var dataDisposable: Disposable? = null
    private var stateDisposable: Disposable? = null

    override fun onCreate(view: IMainView) {
        super.onCreate(view)
        addSubscriptions()
    }

    override fun releaseView() {
        clearSubscriptions()
        super.releaseView()
    }

    override fun onStopClick() {
        analyzer.stopAnalyzing()
    }

    override fun onStartClick(url: String, query: String, threadsCount: Int, urlsToAnalyzeCount: Int) {
        val trimmedQuery = query.trim()
        if (!Page.urlRegex.matches(url)) {
            showMessage(R.string.err_wrong_url_format)
        } else if (trimmedQuery.isEmpty()) {
            showMessage(R.string.err_empty_query)
        } else {
            onNextData(BfsPageAnalyzer.Data(emptyList(), 1))
            analyzer.startAnalyzing(
                    BfsPageAnalyzer.AnalyzeParameters(
                            Page(url, query),
                            urlsToAnalyzeCount,
                            threadsCount
                    )
            )
        }
    }

    private fun onNextData(data: BfsPageAnalyzer.Data) {
        view?.let {
            it.updateList(data.pages)
            it.updateProgress(data.pages.size, data.max)
        }
    }

    private fun onStateChanged(state: BfsPageAnalyzer.State) {
        view?.updateState(state)
    }

    private fun addSubscriptions() {
        dataDisposable = analyzer.getDataObservable(MIN_UPDATE_PERIOD_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onNextData, ::showMessage)

        stateDisposable = analyzer.getStateObservable()
                .subscribe(::onStateChanged)
    }

    private fun clearSubscriptions() {
        dataDisposable?.dispose()
        stateDisposable?.dispose()
    }

    companion object {
        const val MIN_UPDATE_PERIOD_MILLIS = 1000L
    }
}