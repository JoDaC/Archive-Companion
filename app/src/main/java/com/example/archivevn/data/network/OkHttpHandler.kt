package com.example.archivevn.data.network

import android.util.Log
import it.skrape.core.document
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.a
import it.skrape.selects.html5.p
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.dankito.readability4j.Readability4J
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URLEncoder

class OkHttpHandler(url: String) {

    private var client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()
//
//    data class MyDataClass(
//        var httpStatusCode: Int = 0,
//        var httpStatusMessage: String = "",
//        var paragraph: String = "",
//        var allParagraphs: List<String> = emptyList(),
//        var allLinks: List<String> = emptyList()
//    )
//
//    suspend fun extractArticleFromUrl(url: String) {
//        return withContext(Dispatchers.Default) {
//            val extracted = skrape(HttpFetcher) {
//                request {
//                    this.url = url
//                }
//
//                extractIt<MyDataClass> {
//                    it.httpStatusCode = statusCode
//                    it.httpStatusMessage = statusMessage.toString()
//                    htmlDocument {
//                        it.allParagraphs = p { findAll { eachText } }
//                        it.paragraph = p { findFirst { text } }
//                        it.allLinks = a { findAll { eachHref } }
//                    }
//                }
//                // will print:
//                // MyDataClass(httpStatusCode=200, httpStatusMessage=OK, paragraph=i'm a paragraph, allParagraphs=[i'm a paragraph, i'm a second paragraph], allLinks=[http://some.url, http://some-other.url])
//            }
//            print(extracted)
//        }
//    }

    /**
     * Loads the specified URL using the OkHttp client and returns the parsed HTML body as a string.
     * @return The parsed HTML body as a string.
     */
    suspend fun loadUrlAndParseToString(): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            Log.d("Parsed Body", parsedBody.toString())
//            val articleBody = parsedBody.select("[name='articleBody']")
//            Log.d("Article Body", parsedBody.toString())
            response.body()?.close()
            parsedBody.toString()
        }
    }

    suspend fun fetchExtractedPage(url: String): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val html = response.body()?.string() ?: ""
            // Use Readability4J to extract the relevant content
            val readability4J = Readability4J(url, html)
            val article = readability4J.parse()
            val extractedContentHtml = article.content
            extractedContentHtml
        } ?: ""
    }

    // OPTIONAL FUNCTION TO RETURN TITLE AND STRING SEPARATELY
    suspend fun fetchExtractedTitleAndText(url: String): Pair<String?, String?> {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val html = response.body()?.string() ?: ""
            // Use Readability4J to extract the relevant content
            val readability4J = Readability4J(url, html)
            val article = readability4J.parse()
            val extractedText = article.textContent
            Log.i("ExtractedText", extractedText!!)
            val extractedTitle = article.title
            Log.i("ExtractedTitle", extractedTitle!!)
            Pair(extractedText, extractedTitle)
        }
    }

    /**
     * Loads the specified URL using the OkHttp client and searches for specific terms in the
     * response body, indicating whether the page is already archived, needs to be archived,
     * or cannot be archived.
     *
     * @return A string indicating whether the page is already archived, needs to be archived, or cannot be archived.
     */
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
            response.body()?.close()
            resultString
        }
    }

    /**
     * Launches the page archival process using the specified URL and the archive.ph service.
     *
     * @param url The URL to archive.
     * @return The URL of the archived page.
     */
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

            val requestTwo = Request.Builder()
                .url(fullRequestString)
                .build()
            var responseTwo = client.newCall(requestTwo).execute()
            while (responseTwo.toString().contains("https://archive.ph/submit/?submitid=")) {
                responseTwo = client.newCall(requestTwo).execute()
                Log.i("big_ass_waffles", responseTwo.toString())
                // Currently using a very large polling time to avoid captcha.
                delay(30000)
            }
            val urlToTriggerArchival = responseTwo.request().url().toString()
            Log.i("URL to trigger Archival ", urlToTriggerArchival)
            responseOne.body()?.close()
            responseTwo.body()?.close()
            urlToTriggerArchival
        }
    }
}