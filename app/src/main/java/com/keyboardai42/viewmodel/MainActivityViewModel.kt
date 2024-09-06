package com.keyboardai42.viewmodel

import android.app.Application
import android.media.MediaRecorder
import android.os.Build
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keyboardai42.network.APIInterface
import com.keyboardai42.network.RetrofitClient
import com.keyboardai42.data.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    companion object{
        const val DELETE_CHAR = "-1"
        const val RETURN_CHAR = "return"
        const val CHANGE_KEYBOARD = "change"
        const val SHOW_DIALOG = "show dialog"
        const val CHANGE_KEYBOARD_TO_ABC = "change_to_abc"
    }

    val message = MutableLiveData<String>()
    var capsButton : ObservableBoolean = ObservableBoolean(false)

    fun aButtonClick(view: View){
        message.postValue("a")
    }
    fun sButtonClick(view: View){
        message.postValue("s")
    }
    fun dButtonClick(view: View){
        message.postValue("d")
    }
    fun fButtonClick(view: View){
        message.postValue("f")
    }
    fun gButtonClick(view: View){
        message.postValue("g")
    }
    fun hButtonClick(view: View){
        message.postValue("h")
    }
    fun jButtonClick(view: View){
        message.postValue("j")
    }
    fun kButtonClick(view: View){
        message.postValue("k")
    }
    fun lButtonClick(view: View){
        message.postValue("l")
    }
    fun zButtonClick(view: View){
        message.postValue("z")
    }
    fun xButtonClick(view: View){
        message.postValue("x")
    }
    fun cButtonClick(view: View){
        message.postValue("c")
    }
    fun vButtonClick(view: View){
        message.postValue("v")
    }
    fun bButtonClick(view: View){
        message.postValue("b")
    }
    fun nButtonClick(view: View){
        message.postValue("n")
    }
    fun mButtonClick(view: View){
        message.postValue("m")
    }
    fun commaButtonClick(view: View){
        message.postValue(",")
    }
    fun spaceButtonClick(view: View){
        message.postValue(" ")
    }
    fun dotButtonClick(view: View){
        message.postValue(".")
    }
    fun qButtonClick(view: View){
        message.postValue("q")
    }
    fun wButtonClick(view: View){
        message.postValue("w")
    }
    fun eButtonClick(view: View){
        message.postValue("e")
    }
    fun rButtonClick(view: View){
        message.postValue("r")
    }
    fun tButtonClick(view: View){
        message.postValue("t")
    }
    fun yButtonClick(view: View){
        message.postValue("y")
    }
    fun uButtonClick(view: View){
        message.postValue("u")
    }
    fun iButtonClick(view: View){
        message.postValue("i")
    }
    fun oButtonClick(view: View){
        message.postValue("o")
    }
    fun pButtonClick(view: View){
        message.postValue("p")
    }
    fun delButtonClick(view: View){
        message.postValue(DELETE_CHAR)
    }
    fun returnButtonClick(view: View){
        message.postValue(RETURN_CHAR)
    }
    fun capsButtonClick(view: View){
        capsButton.set(!capsButton.get())
    }
    fun removeLastChar(s: String?): String? {
        return if (s == null || s.length == 0) {
            s
        } else s.substring(0, s.length - 1)
    }

    fun oneLongButtonClick(view: View): Boolean {
        message.postValue("1")
        return true
    }
    fun twoLongButtonClick(view: View): Boolean {
        message.postValue("2")
        return true
    }
    fun threeLongButtonClick(view: View): Boolean {
        message.postValue("3")
        return true
    }
    fun fourLongButtonClick(view: View): Boolean {
        message.postValue("4")
        return true
    }
    fun fiveLongButtonClick(view: View): Boolean {
        message.postValue("5")
        return true
    }
    fun sixLongButtonClick(view: View): Boolean {
        message.postValue("6")
        return true
    }
    fun sevenLongButtonClick(view: View): Boolean {
        message.postValue("7")
        return true
    }
    fun eightLongButtonClick(view: View): Boolean {
        message.postValue("8")
        return true
    }
    fun nineLongButtonClick(view: View): Boolean {
        message.postValue("9")
        return true
    }
    fun zeroLongButtonClick(view: View): Boolean {
        message.postValue("0")
        return true
    }
    fun changeButtonClick(view: View){
        message.postValue(CHANGE_KEYBOARD)
    }
    fun changeAbcButtonClick(view: View){
        message.postValue(CHANGE_KEYBOARD_TO_ABC)
    }
    fun recordButtonClick(view: View){
        surroundingSound()
    }
    fun minusButtonClick(view: View){
        message.postValue("-")
    }
    fun slashButtonClick(view: View){
        message.postValue("/")
    }
    fun twoDotButtonClick(view: View){
        message.postValue(":")
    }
    fun semicolonButtonClick(view: View){
        message.postValue(";")
    }
    fun left_parenthesisButtonClick(view: View){
        message.postValue("(")
    }
    fun right_parenthesisButtonClick(view: View){
        message.postValue(")")
    }
    fun andButtonClick(view: View){
        message.postValue("&")
    }
    fun plusButtonClick(view: View){
        message.postValue("+")
    }
    fun braceButtonClick(view: View){
        message.postValue("\"")
    }
    fun transcriptionDetailButtonClick(view: View){
        message.postValue(SHOW_DIALOG)
    }
    private fun surroundingSound() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
        val now = Date()
        // Record to the external cache directory for visibility
        // Record to the external cache directory for visibility
        var fileName = application.applicationContext.externalCacheDir?.absolutePath
        fileName = fileName.plus("/" + formatter.format(now) + ".mp3")
        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(application.applicationContext)
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
        Toast.makeText(application.applicationContext,"Recording...", Toast.LENGTH_LONG).show()
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
                        message.postValue(response.body()?.transcription.toString())
                    }

                    override fun onFailure(call: Call<Message>, t: Throwable) {
                        Toast.makeText(application.applicationContext,"Something went wrong!", Toast.LENGTH_LONG).show()
                    }

                })
            }
        }.start()
    }
}