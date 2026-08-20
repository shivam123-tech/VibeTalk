package com.example.vibetalk

import androidx.lifecycle.ViewModel
import com.example.vibetalk.rtc.RTCManager
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.vibetalk.CallUIState
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import im.zego.zegoexpress.constants.ZegoRemoteDeviceState
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class CallViewModel(
    val rtcManager: RTCManager
           ) : ViewModel() {

    private  val _uiState = MutableStateFlow(CallUIState());

   private val _remoteCallEnded = MutableSharedFlow<Unit>()
    val uiState = _uiState.asStateFlow();
    val remoteCallEnded = _remoteCallEnded.asSharedFlow()

    init {
        viewModelScope.launch{
            rtcManager.remoteStream.collect { streamID ->
                if (streamID != "") {
                    _uiState.value = _uiState.value.copy(
                        remoteStreamId = streamID,
                        showRemoteVideo = true,

                    )
                } else{
                    _uiState.value = _uiState.value.copy(
                        remoteStreamId = streamID,
                        showRemoteVideo = false
                    )
                }
            }
    }

        viewModelScope.launch {
            rtcManager.remoteCamera.collect { remoteCamera ->
                _uiState.value = _uiState.value.copy(
                    remoteCameraState = remoteCamera
                )

            }
        }

        viewModelScope.launch {
            rtcManager.remoteCallEnded.collect {
                _remoteCallEnded.emit(Unit)
            }
        }



    }
}