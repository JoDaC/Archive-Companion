package com.example.archivevn.data

data class HistoryItem(
    val title: String,
    val url: String,
    val archivedUrl: String,
    val subtitle: String,
    val isReaderMode: Boolean = false
)