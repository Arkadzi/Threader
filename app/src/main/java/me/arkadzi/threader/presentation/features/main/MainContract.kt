package me.arkadzi.threader.presentation.features.main

import me.arkadzi.threader.domain.model.Page
import me.arkadzi.threader.domain.processing.BfsPageAnalyzer
import me.arkadzi.threader.presentation.presenter.Presenter
import me.arkadzi.threader.presentation.view.View

interface IMainView : View {
    fun updateProgress(progress: Int, max: Int)
    fun updateList(data: List<Page>)
    fun updateState(state: BfsPageAnalyzer.State)
}

interface IMainPresenter : Presenter<IMainView> {
    fun onStartClick(url: String, query: String, threadsCount: Int, urlsToAnalyzeCount: Int)
    fun onStopClick()
}