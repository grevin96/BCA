package com.bca.music.model

data class BaseResponse<T>(
    val resultCount: Int,
    val results: ArrayList<T>
)