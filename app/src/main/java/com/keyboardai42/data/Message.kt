package com.keyboardai42.data

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("transcription")
    val transcription: String,

    @SerializedName("Answer")
    val answer: String,

    @SerializedName("error")
    val error : String
)