package me.arkadzi.threader.domain.processing

interface PageLoader {
    fun loadPage(url: String) : String
}