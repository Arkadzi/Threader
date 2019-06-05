package me.arkadzi.threader.data

import me.arkadzi.threader.domain.processing.PageLoader
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class OkPageLoader : PageLoader {
    var client = OkHttpClient.Builder()
        .callTimeout(10000, TimeUnit.MILLISECONDS)
        .followSslRedirects(false)
        .followRedirects(false)
        .build()

    override fun loadPage(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        return client.newCall(request)
            .execute().body!!.string()
    }
}