package me.arkadzi.threader.domain.model

import android.support.annotation.WorkerThread
import me.arkadzi.threader.domain.processing.PageLoader

class Page(
    val url: String,
    val query: String,
    parent: Page? = null
) {
    val layer: Int = (parent?.layer ?: 0) + 1
    var status: Status = Status.PENDING
    var error: Throwable? = null
    val urls = mutableListOf<String>()
    var analyzeTime: Long = 0

    @WorkerThread
    fun process(pageLoader: PageLoader) {
        val startTime = System.currentTimeMillis()
        try {
            status = Status.RUNNING
            val text = pageLoader.loadPage(url)
            status = getResultStatus(text)
            updateUrls(text)
        } catch (e: Exception) {
            status = Status.FAILURE
            error = e
        }
        analyzeTime = System.currentTimeMillis() - startTime
    }

    private fun updateUrls(text: String) {
        val matches = urlRegex.findAll(text)
        urls.clear()
        urls.addAll(matches.map { matcher -> matcher.groupValues[0] }.distinct())
    }

    private fun getResultStatus(text: String): Status {
        return if (text.contains(query, ignoreCase = true)) {
            Status.SUCCESS
        } else {
            Status.NOT_FOUND
        }
    }

    fun getStates(): List<Page> {
        return urls.map { Page(it, query, this) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Page

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    companion object {
        val urlRegex =
            Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
    }
}

enum class Status {
    PENDING,
    RUNNING,
    SUCCESS,
    NOT_FOUND,
    FAILURE
}