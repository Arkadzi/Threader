package me.arkadzi.threader.model

import android.support.annotation.WorkerThread

class Page(
        val url: String,
        val pageProcessor: PageProcessor,
        val query: String
) : QueueElement {
    var status: Status = Status.PENDING
    var error: Exception? = null
    val urls = mutableListOf<String>()
    override var layer: Int = 1
    override var parent: QueueElement?
        get() = _parent
        set(value) {
            _parent = value
            layer = value?.layer ?: 0 + 1
        }
    var text: String? = null
    var _parent: QueueElement? = null
    var analyzeTime: Long = 0

    @WorkerThread
    override fun process() {
        val startTime = System.currentTimeMillis()
        try {
            status = Status.RUNNING
            text = pageProcessor.getPageContent(url)
            val matches = urlRegex.findAll(text!!)
            status = if (text!!.contains(query, ignoreCase = true)) {
                Status.SUCCESS
            } else {
                Status.NOT_FOUND
            }
            urls.clear()
            urls.addAll(matches.map { matcher -> matcher.groupValues[0] }.distinct())
        } catch (e: Exception) {
            status = Status.FAILURE
            error = e
        }
        analyzeTime = System.currentTimeMillis() - startTime
    }

    override fun getStates(): List<QueueElement> {
        return urls.map { Page(it, pageProcessor, query) }
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
        val urlRegex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
    }
}

enum class Status {
    PENDING,
    RUNNING,
    SUCCESS,
    NOT_FOUND,
    FAILURE
}