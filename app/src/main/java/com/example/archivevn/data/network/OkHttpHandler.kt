package com.example.archivevn.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.dankito.readability4j.Readability4J
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.net.SocketTimeoutException
import java.net.URLEncoder

class OkHttpHandler(url: String) {

    private var client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()

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

    /**
     *
     * Fetches the extracted title, text, and images for a given URL using Readability4J library.
     * If a SocketTimeoutException occurs, the function will retry the request up to 2 times.
     * @param url The URL to fetch the content from.
     * @return A Triple object containing the article text, extracted title, and a list of image URLs.
     */
    suspend fun fetchExtractedTitleAndText(url: String): Triple<String?, String?, String?> {
        return withContext(Dispatchers.Default) {
            var retryCount = 0
            var extractedText: String?
            var extractedTitle: String? = null
            var extractedSubtitle: String? = null
            var articleHtml: String?
            var articleText: String? = null
            while (retryCount < 2) {
                try {
                    val response = client.newCall(request).execute()
                    val html = response.body()?.string() ?: ""
                    // Use Readability4J to extract the relevant content
                    val readability4J = Readability4J(url, html)
                    val article = readability4J.parse()
                    extractedText = article.textContent
                    articleHtml = article.content
                    Log.i("ExtractedText", extractedText!!)
                    Log.i("articleHtml", articleHtml!!)
                    extractedTitle = article.title
                    extractedSubtitle = article.excerpt
                    if (extractedSubtitle.isNullOrBlank()) {
                        Log.i("ExtractedSubTitle", "Article contains no subtitle excerpt.")
                    }
                    Log.i("ExtractedTitle", extractedTitle!!)
                    Log.i("ExtractedSubTitle", extractedSubtitle!!)
                    val parsedBody = Jsoup.parse(articleHtml)
                    val paragraphs = parsedBody.getElementsByTag("p")
                    val text = StringBuilder()
                    for (paragraph in paragraphs) {
                        // Loop through all the child nodes of the paragraph
                        for (child in paragraph.childNodes()) {
                            // If the child node is a hyperlink, append its text to the final text string
                            if (child is Element && child.tagName() == "a") {
                                text.append(child.text())
                            } else if (child is TextNode) {
                                text.append(child.text())
                            }
                        }
                        // Append two line breaks after each paragraph
                        text.append("\n\n")
                    }
                    articleText = text.toString()
                    break // Exit the loop if the request succeeds
                } catch (e: SocketTimeoutException) {
                    // If the request times out, retry.
                    retryCount++
                    Log.e("FetchTitleAndText", "Request timed out. Retrying ($retryCount/2)...")
                    continue
                }
            }
            Triple(articleText, extractedTitle, extractedSubtitle)
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
}