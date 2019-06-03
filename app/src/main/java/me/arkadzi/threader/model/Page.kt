package me.arkadzi.threader.model

class Page(val url: String, val pageProcessor: PageProcessor, val query: String, _parent: Page?) : QueueElement {
    override val parent = _parent
    override val layer: Int = (_parent?.layer ?: 0) + 1
    var status: Status = Status.PENDING
    val urls = mutableListOf<String>()
    //    var text: String? = null
    var analyzeTime: Long = 0

    override fun process() {
        val startTime = System.currentTimeMillis()
        try {
            status = Status.RUNNING
//            println("handle 1 $url")
            val text = pageProcessor.getPageContent(url)
//            println("handle 2 $url")

            val matches = urlRegex.findAll(text)
            status = if (text.contains(query, ignoreCase = true)) {
                Status.SUCCESS
            } else {
                Status.NOT_FOUND
            }

            urls.clear()
            urls.addAll(matches.map { matcher -> matcher.groupValues[0] }.distinct())

        } catch (e: Exception) {
            status = Status.FAILURE
        }
        analyzeTime = System.currentTimeMillis() - startTime
    }

    override fun getStates(): List<Page> {
        return urls.map { Page(it, pageProcessor, query, this) }
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