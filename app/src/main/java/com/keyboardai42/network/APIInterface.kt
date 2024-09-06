package com.keyboardai42.network

import com.keyboardai42.data.Languages
import com.keyboardai42.data.Message
import com.keyboardai42.data.Translate
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface APIInterface {

    @Multipart
    @POST("upload")
    fun send_recording(
        @Part audio: MultipartBody.Part?
    ): Call<Message>

    @FormUrlEncoded
    @POST("upload")
    fun sendMessage(
        @Field("query") message: String
    ):Call<Message>

    @FormUrlEncoded
    @POST("translate")
    fun getTranslatedText(
        @Field("lang") lang: String,
        @Field("text") text: String
    ):Call<Translate>

    @GET("supportted_lang")
    fun getLanguageList():Call<Languages>
}