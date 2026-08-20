package com.example.vibetalk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vibetalk.rtc.RTCManager

class CallViewModelFactory (
    private val rtcManager: RTCManager
) : ViewModelProvider.Factory{

    override fun<T: ViewModel> create(
        modelClass: Class<T>
    ): T{
        if(modelClass.isAssignableFrom(CallViewModel::class.java)){
            return CallViewModel(rtcManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}