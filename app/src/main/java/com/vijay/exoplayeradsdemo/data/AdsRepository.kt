package com.vijay.exoplayeradsdemo.data

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

class AdsRepository {

  suspend fun getAds(): String?{
    val url = URL("https://jioads.akamaized.net/test/androidTest/response.json")
    val httpURLConnection = url.openConnection() as HttpURLConnection
    httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
    httpURLConnection.requestMethod = "GET"
    httpURLConnection.doInput = true
    httpURLConnection.doOutput = false

    // Check if the connection is successful
    val responseCode = httpURLConnection.responseCode
    return if (responseCode == HttpURLConnection.HTTP_OK) {
      val response = httpURLConnection.inputStream.bufferedReader()
        .use { it.readText() }
      response
    } else {
      null
    }
  }
}