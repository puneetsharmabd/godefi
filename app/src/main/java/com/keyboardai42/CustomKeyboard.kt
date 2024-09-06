package com.keyboardai42

import android.inputmethodservice.InputMethodService
import android.view.View

class CustomKeyboard : InputMethodService() {
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.activity_main,null)
    }
}