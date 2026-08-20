package com.example.vibetalk

data class CallUIState (
    val remoteStreamId : String="",   //We are passing here intial value because we are passing these in STATE FLOW and we Require Intial value
    val showRemoteVideo : Boolean=false,
    val remoteCameraState : Boolean = true, //if remoteCameraState is true means remote camera is open and if false remoteCamera is Disable
)