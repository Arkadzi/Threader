package me.arkadzi.threader.data

import me.arkadzi.threader.domain.processing.PageLoader
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class OkPageLoader : PageLoader {
    var client = OkHttpClient.Builder()
            .connectTimeout(2000, TimeUnit.MILLISECONDS)
            .readTimeout(4000, TimeUnit.MILLISECONDS)
            .followSslRedirects(false)
            .followRedirects(false)
            .build()


//        https://production.bbc.co.uk/isite2/contentreader/xml/result
    override fun loadPage(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        return client.newCall(request)
            .execute().body!!.string()
    }
}