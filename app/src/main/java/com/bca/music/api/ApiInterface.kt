package com.bca.music.api

import com.bca.music.model.BaseResponse
import com.bca.music.model.Item
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("search?media=music")
    fun search(@Query("term") term: String?): Call<BaseResponse<Item>>
}