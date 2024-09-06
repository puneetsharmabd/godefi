package com.keyboardai42.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.keyboardai42.AudioResultCallback
import com.keyboardai42.LanguageResultCallback
import com.keyboardai42.data.Languages
import com.keyboardai42.data.Message
import com.keyboardai42.data.Translate
import com.keyboardai42.network.APIInterface
import com.keyboardai42.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        fun hideKeyboard(window: Window) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
        }

        fun askAI(context: Context,message: String,audioResult: AudioResultCallback){
            val apiInterface = RetrofitClient.getClient()?.create(APIInterface::class.java)
            val sendMessage =
                apiInterface?.sendMessage(message)
            sendMessage?.enqueue(object : Callback<Message>{
                override fun onResponse(call: Call<Message>, response: Response<Message>) {

                    audioResult.audioResult(response.body()?.answer)
                }

                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Log.e("++++",t.toString())
                }

            })
        }

        fun getTranslatedText(lang: String , text: String, audioResult: AudioResultCallback){
            val apiInterface = RetrofitClient.getClient()?.create(APIInterface::class.java)
            val getTranslate = apiInterface?.getTranslatedText(lang,text)
            getTranslate?.enqueue(object : Callback<Translate>{
                override fun onResponse(call: Call<Translate>, response: Response<Translate>) {
                    audioResult.audioResult(response.body()?.translated_text)
                }

                override fun onFailure(call: Call<Translate>, t: Throwable) {
                    Log.e("++++",t.toString())
                }

            })
        }

         fun surroundingSound(context: Context,audioResult : AudioResultCallback){
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            val now = Date()
            // Record to the external cache directory for visibility
            // Record to the external cache directory for visibility
            var fileName = context.externalCacheDir?.absolutePath
            fileName = fileName.plus("/" + formatter.format(now) + ".mp3")
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setOutputFile(fileName as String)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                recorder.prepare()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            recorder.start()
            Toast.makeText(context, "Recording...", Toast.LENGTH_LONG).show()
            val countDownTimer = object : CountDownTimer(10000, 1000) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    recorder.stop()
                    recorder.release()
                    val file: File = File(fileName as String)
                    val filePart = MultipartBody.Part.createFormData(
                        "file", file.name, RequestBody.create(
                            MediaType.parse("audio/*"), file
                        )
                    )
                    val apiInterface = RetrofitClient.getClient()?.create(APIInterface::class.java)
                    val sendRecording =
                        apiInterface?.send_recording(filePart)
                    sendRecording?.enqueue(object : Callback<Message> {
                        override fun onResponse(
                            call: Call<Message>,
                            response: retrofit2.Response<Message>
                        ) {
//                            Toast.makeText(
//                                context,
//                                "" + response.body()?.transcription.toString(),
//                                Toast.LENGTH_LONG
//                            ).show()
                            audioResult.audioResult(response.body()?.transcription.toString())
                            //message.postValue(response.body()?.transcription.toString())
                        }

                        override fun onFailure(call: Call<Message>, t: Throwable) {
                            audioResult.audioResult("Something went wrong!")
//                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG)
//                                .show()
                        }

                    })
                }
            }.start()
        }

         fun getLanguageList(languageList : LanguageResultCallback){
            val apiInterface = RetrofitClient.getClient()?.create(APIInterface::class.java)
            val languages = apiInterface?.getLanguageList()
            languages?.enqueue(object : Callback<Languages>{
                override fun onResponse(call: Call<Languages>, response: Response<Languages>) {
                    if (response.body()?.languages != null && response.body()?.languages?.size!! >0){
                        languageList.audioResult(response.body()?.languages!!)
                    }
                }

                override fun onFailure(call: Call<Languages>, t: Throwable) {

                }

            })
        }
    }
}