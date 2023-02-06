package com.example.archivevn

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URLEncoder

class OkHttpHandler(url: String) {

    private var client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()

    suspend fun loadUrlAndParseToString(): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            Log.d("Parsed Body", parsedBody.toString())
//            val articleBody = parsedBody.select("[name='articleBody']")
            Log.d("Article Body", parsedBody.toString())
            parsedBody.toString()
        }
    }

    suspend fun loadUrl(): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            val searchTerms =
                listOf("No results", "Newest", "My url is alive and I want to archive its content")
            var resultString = ""
            for (searchTerm in searchTerms) {
                if (responseBody != null && responseBody.contains(searchTerm)) {
                    resultString = searchTerm
                    break
                }
            }
            if (resultString.isEmpty()) {
                Log.d("No search terms found in response body", responseBody!!)
            }
            resultString
        }
    }

    suspend fun launchPageArchival(url: String): String {
        return withContext(Dispatchers.IO) {
            val responseOne = client.newCall(request).execute()
            val responseBody = responseOne.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            val submitId = parsedBody.select("[name='submitId']").first()?.attr("value")
            Log.i("submitId is ", submitId!!)
            val encodedUrl = URLEncoder.encode(url, "UTF-8")
            val fullRequestString = "https://archive.ph/submit/?submitid=$submitId&url=$encodedUrl"
            Log.i("fullRequestString is ", fullRequestString)

            val requestTwo = Request.Builder().url("https://archive.ph/submit/?submitid=o1JI3nhGzEPoSn04yE+Jg1aVrslo8BOz5+ASKiAlNzQwIw/3uAFHCc4XEUlx8eoj&url=https%3A%2F%2Fwww.amazon.com%2Fdp%2FB093HBBMPT%2Fref%3Dvp_m_cpf-substitute-widget_pd%3F_encoding%3DUTF8%26pf_rd_p%3D574aa16c-0d12-41ed-9966-73e468eecdcd%26pf_rd_r%3DS9YCS4F7NBD10VNJT8SE%26pd_rd_wg%3DVmvky%26pd_rd_i%3DB093HBBMPT%26pd_rd_w%3DVgZD1%26content-id%3Damzn1.sym.574aa16c-0d12-41ed-9966-73e468eecdcd%26pd_rd_r%3Dbf2dc4d3-c9de-4822-9201-55849520dad2").build()
            // HARDCODING THE URL HERE WORKED! ^^^ REPLACE WITH fullRequestString TO GO BACK TO HOW IT WAS

            //MAYBE TRY POLLING val responseTwo = client.newCall(requestTwo).execute() INSTEAD
            val responseTwo = client.newCall(requestTwo).execute()
            Log.i("big_ass_waffles", responseTwo.toString())
            var urlToTriggerArchival = responseTwo.request().url().toString()
            while (urlToTriggerArchival.contains("https://archive.ph/submit/?submitid=")) {
                urlToTriggerArchival = responseTwo.request().url().toString()
                Log.i("URL to trigger Archival ", urlToTriggerArchival)
                delay(1000)
            }

            Log.i("URL to trigger Archival ", urlToTriggerArchival)
            // Requesting URL from same response after 30 sec wait to see if results in short URL bring delivered.
//            delay(30000)
//            val shortURL = responseTwo.request().url().toString()
//            Log.i("Final URL is ", shortURL)
            urlToTriggerArchival
        }
    }
}