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

//    http://imyerevan.com/pdf/en/2006-best.pdf
//    https://kclpure.kcl.ac.uk/portal/files/106425259/2019_Mowlem_Florence_1414261_ethesis.pdf
    //        https://production.bbc.co.uk/isite2/contentreader/xml/result

    override fun loadPage(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        return client.newCall(request)
            .execute().body!!.string()
    }
}