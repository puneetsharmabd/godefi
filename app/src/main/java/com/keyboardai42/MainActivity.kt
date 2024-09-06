package com.keyboardai42

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.keyboardai42.databinding.ActivityMainBinding
import com.keyboardai42.dialog.ShowProgress
import com.keyboardai42.utils.Utils
import com.keyboardai42.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    val viewModel : MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.viewModel = viewModel
        binding.rowTwo.viewModel = viewModel
        binding.rowThree.viewModel = viewModel
        binding.rowFour.viewModel = viewModel
        binding.rowFive.viewModel = viewModel
        binding.rowAudio.viewModel = viewModel
        binding.rowThree2.viewModel = viewModel
        binding.rowFive2.viewModel = viewModel
        //setListener()
        //setObserver()
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf<String>(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            100
        )
    }

    private fun setListener(){
        binding.et.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                binding.keyboardLayout.visibility = View.VISIBLE
                binding.keyboardLayout.animate().alpha(1.0f)
            }
        }
    }
    private fun setObserver(){
        viewModel.message.observe(this){
            when (it){
                MainActivityViewModel.DELETE_CHAR -> {
                    binding.et.setText(viewModel.removeLastChar(binding.et.text.toString()))
                    binding.et.setSelection(binding.et.length())
                }
                MainActivityViewModel.RETURN_CHAR -> {
                    binding.et.setText(binding.et.text.toString()+"\n")
                    binding.et.setSelection(binding.et.length())
                }
                MainActivityViewModel.CHANGE_KEYBOARD -> {
                    binding.keyboardLayout.visibility = View.GONE
                    binding.keyboardLayout2.visibility = View.VISIBLE
                }
                MainActivityViewModel.CHANGE_KEYBOARD_TO_ABC -> {
                    binding.keyboardLayout.visibility = View.VISIBLE
                    binding.keyboardLayout2.visibility = View.GONE
                }
                MainActivityViewModel.SHOW_DIALOG -> {
                    val showProgress = ShowProgress(this@MainActivity)
                    showProgress.showPopup()
                }
                else -> {
                    binding.et.text = binding.et.text?.append(it)
                    binding.et.setSelection(binding.et.length())
                }
            }
        }
    }
}