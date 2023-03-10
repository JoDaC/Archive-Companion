# Archive Companion
 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://github.com/JoDaC/Archive-Companion/actions/workflows/build.yml/badge.svg)](https://github.com/JoDaC/Archive-Companion/actions/workflows/build.yml)

Archive Companion is an Android app that allows users to archive and read articles from the web in a simple, easy-to-use format. The app leverages the archive functions of archive.today to make archiving and accessing archived articles from the web easy.

## Features
- Archive any URL from the web. 
- Read archived pages in a simple, easy-to-read format using 'Reader' mode
- Share URLs using Android's share sheet function to quickly pass them to the app.
- Also serves as a means to bypass most paywalls you may encounter on a webpage. ;-)

## Implementation Details
- Uses OkHttp and regular expressions to scrape Archive.today and extract article text
- Uses Data Binding to bind UI components to the layout
- Uses MVVM and LiveData to manage data and UI states
- Uses Kotlin coroutines to perform network and I/O operations on background threads

## Third Party Libraries

- JSoup for parsing web text. Useful in scraping parts of Archive.today -- org.jsoup:jsoup:1.15.3
- androidx.databinding:databinding-runtime:7.1.2
- Justified TextView, -- com.codesgood:justifiedtextview:2.0.1
- GSON -- com.google.code.gson:gson:2.10.1
- Lottie Animations -- com.airbnb.android:lottie:6.0.0
- keyboardvisibilityevent - handle soft keyboard visibility change events -- net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:2.2.0
- AppIntro, which provides an easy to implementation for launch app introduction carousel. -- com.github.AppIntro:AppIntro:6.2.0
- Readability4J - Parses online articles to remove the parts you don't want. E.g, ads, other copy. -- net.dankito.readability4j:readability4j:1.0.8
