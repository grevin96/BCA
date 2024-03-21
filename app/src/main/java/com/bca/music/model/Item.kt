package com.bca.music.model

data class Item(
    val wrapperType: String?,
    val trackId: String?,
    val artisId: String?,
    val collectionId: String?,
    val artistName: String?,
    val collectionName: String?,
    val collectionCensoredName: String?,
    val artistViewUrl: String?,
    val collectionViewUrl: String?,
    val artworkUrl60: String?,
    val artworkUrl100: String?,
    val collectionPrice: String?,
    val collectionExplicitness: String?,
    val primaryGenreName: String?,
    val previewUrl: String?,
    val description: String?,
    val trackName: String?,
    val trackTimeMillis: Long
)
