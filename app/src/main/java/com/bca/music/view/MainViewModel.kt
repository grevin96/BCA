package com.bca.music.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bca.music.api.ApiClient
import com.bca.music.model.BaseResponse
import com.bca.music.model.Item
import retrofit2.Call
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val dataLiveData                        = MutableLiveData<ArrayList<Item>>()
    private val progressLiveData                    = MutableLiveData<Boolean>()
    private val failureLiveData                     = MutableLiveData<Boolean>()
    private var callData: Call<BaseResponse<Item>>? = null

    fun observerData(): LiveData<ArrayList<Item>>   = dataLiveData
    fun observerProgress(): LiveData<Boolean>       = progressLiveData
    fun observerFailure(): LiveData<Boolean>        = failureLiveData

    fun data(search: String?) {
        callData                = ApiClient.api.search(search?.replace(" ", "+"))
        progressLiveData.value  = true
        failureLiveData.value   = false

        callData?.enqueue(object: retrofit2.Callback<BaseResponse<Item>> {
            override fun onResponse(call: Call<BaseResponse<Item>>, response: Response<BaseResponse<Item>>) {
                progressLiveData.value  = false

                if (response.body() != null) dataLiveData.value = response.body()!!.results
            }

            override fun onFailure(call: Call<BaseResponse<Item>>, t: Throwable) {
                if (!call.isCanceled) failureLiveData.value = true
            }
        })
    }

    fun cancel() { callData?.cancel() }
}