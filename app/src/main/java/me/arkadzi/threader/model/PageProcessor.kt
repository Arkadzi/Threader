package me.arkadzi.threader.model

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import okhttp3.OkHttpClient
import android.R.string
import android.os.AsyncTask.execute
import okhttp3.Request


class PageProcessor {
    var client = OkHttpClient()
    private val USER_AGENT = "Mozilla/5.0"

    fun getPageContent(url: String): String {
//
//        val obj = URL(url)
//        val con = obj.openConnection() as HttpURLConnection
//
//        con.requestMethod = "GET"
//
//        con.setRequestProperty("User-Agent", USER_AGENT)
//
//        val responseCode = con.responseCode
////        println("\nSending 'GET' request to URL : $url")
////        println("Response Code : $responseCode")
//
//        val `in` = BufferedReader(
//                InputStreamReader(con.inputStream))
//        var inputLine: String? = null
//        val response = StringBuffer()
//
//        while ((`in`.readLine()?.let { inputLine = it }) != null) {
//            response.append(inputLine)
//        }
////        println(response)
//        `in`.close()
//
//        return response.toString()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }
}