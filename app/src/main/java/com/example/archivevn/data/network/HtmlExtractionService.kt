package com.example.archivevn.data.network

import android.util.Log
import com.example.archivevn.data.SkrapeDataClass
import it.skrape.core.document
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.ElementNotFoundException
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.a
import it.skrape.selects.html5.p

//class HtmlExtractionService {
//    fun extractArticleFromUrl(url: String) {
//        val extracted = skrape(HttpFetcher) {
//            request {
//                this.url = url
//            }
//
//            response {
//                SkrapeDataClass(
//                    allParagraphs = document.p { findAll { eachText } },
//                    paragraph = document.p { findFirst { text } },
//                    allLinks = document.a { findAll { eachHref } }
//                )
//            }
//        }
//        println(extracted)
//        // will print:
//        // MyDataClass(httpStatusCode=200, httpStatusMessage=OK, paragraph=i'm a paragraph, allParagraphs=[i'm a paragraph, i'm a second paragraph], allLinks=[http://some.url, http://some-other.url])
//    }
//
//}

class HtmlExtractionService {
    suspend fun extractArticleFromUrl(url: String): SkrapeDataClass {
        return skrape(HttpFetcher) {
            request {
                this.url = url
            }

            response {
                SkrapeDataClass(
                    allParagraphs = document.p { findAll { eachText } },
                    paragraph = document.p { findFirst { text } },
                    allLinks = document.a { findAll { eachHref } }
                )
            }
        }
    }
}
