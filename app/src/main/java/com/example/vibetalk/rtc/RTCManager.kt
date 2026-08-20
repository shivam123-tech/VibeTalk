package com.example.vibetalk.rtc
import android.util.Log
import im.zego.zegoexpress.ZegoExpressEngine
import im.zego.zegoexpress.callback.IZegoEventHandler
import im.zego.zegoexpress.constants.ZegoUpdateType
import im.zego.zegoexpress.entity.ZegoCanvas
import im.zego.zegoexpress.entity.ZegoUser
import im.zego.zegoexpress.entity.ZegoRoomConfig
import im.zego.zegoexpress.entity.ZegoStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.util.ArrayList
import im.zego.zegoexpress.constants.ZegoPublishChannel
import im.zego.zegoexpress.constants.ZegoRemoteDeviceState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class RTCManager (

    private val zegoEngine : ZegoExpressEngine
){

    val _remoteStream = MutableStateFlow<String>("")
    val _remoteCamera = MutableStateFlow<Boolean>(true)

    val _remoteCallEnded = MutableSharedFlow<Unit>()
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val remoteStream = _remoteStream.asStateFlow();
    val remoteCamera = _remoteCamera.asStateFlow();
    val remoteCallEnded = _remoteCallEnded.asSharedFlow()


    fun loginRoom(
        roomId: String,
        userId: String?,
        userName: String,
        onLoginSuccess: () -> Unit
    ) {



        val user = ZegoUser(userId, userName)
        val config = ZegoRoomConfig()



        zegoEngine.loginRoom(
            roomId,
            user,
            config
        ) { errorCode, extendedData ->



            if (errorCode == 0) {

                _remoteCamera.value=true
                onLoginSuccess()

            } else {
            }
        }
        Log.d("CALL_STATE", "LOGIN ROOM: $roomId")

    }

        fun publishStream(
            streamId: String
        ){
            zegoEngine.startPublishingStream(streamId);
        }

        fun startPlayingStream(
            streamId: String,
            canvas: ZegoCanvas
        ){

            zegoEngine.startPlayingStream(
                streamId,
                canvas
            )
        }

    fun stopPlayingStream(
        streamId : String
    ){
        zegoEngine.stopPlayingStream(streamId);
    }

    fun callEnd(
        roomId: String
                ){
        zegoEngine.stopPublishingStream(ZegoPublishChannel.MAIN)
        if(_remoteStream.value!=""){
            zegoEngine.stopPlayingStream(_remoteStream.value)
        }
        zegoEngine.stopPreview()
        _remoteStream.value = ""
        zegoEngine.logoutRoom(roomId)
    }

    fun muteMic(
        mute: Boolean
    ){
        zegoEngine.muteMicrophone(mute)
    }

    fun cameraOff(
        camera: Boolean
    ){
        Log.d("CAMERA_DEBUG", "enableCamera($camera)")

        zegoEngine.enableCamera(camera)
    }


    fun switchCamera(frontCamera: Boolean){
        zegoEngine.useFrontCamera(frontCamera)
    }

        fun listOfStream(){

            val eventhandler = object : IZegoEventHandler() {

                override fun onRoomStreamUpdate(
                    roomID: String?,
                    updateType: ZegoUpdateType?,
                    streamList: ArrayList<ZegoStream?>?,
                    extendedData: JSONObject?
                ) {

                    super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData)



                    if (updateType == ZegoUpdateType.ADD) {


                        streamList?.forEach { stream ->
                            val streamID = stream?.streamID

                            if (streamID != null) {
                                _remoteStream.value = streamID
                            }
                        }
                    }

                    if (updateType == ZegoUpdateType.DELETE) {


                        streamList?.forEach { stream ->
                            val streamID = stream?.streamID
                            if (streamID != null && roomID != null) {
                                zegoEngine.stopPlayingStream(streamID);
                                _remoteStream.value = "";


                                zegoEngine.logoutRoom(roomID) //Now whenre User A cut then User B also come out from room

                                scope.launch {
                                    _remoteCallEnded.emit(Unit)
                                }


                            }
                        }


                    }
                }

                override fun onRemoteCameraStateUpdate(
                    streamID: String?,
                    state: ZegoRemoteDeviceState?
                ) {
                    super.onRemoteCameraStateUpdate(streamID, state)

                    Log.d(
                        "REMOTE_CAMERA",
                        "streamID=$streamID, state=$state"
                    )

                    when(state){

                        ZegoRemoteDeviceState.DISABLE-> {
                            _remoteCamera.value= false
                        }
                        ZegoRemoteDeviceState.OPEN -> {

                                _remoteCamera.value = true

                        }
                        //When Lock Screen
                        ZegoRemoteDeviceState.IN_BACKGROUND ->{
                            _remoteCamera.value = false;
                        }

                        else -> {
                            _remoteCamera.value = false;
                        }
                    }



                }

                override fun onRoomUserUpdate(
                    roomID: String?,
                    updateType: ZegoUpdateType?,
                    userList: ArrayList<ZegoUser?>?
                ) {
                    super.onRoomUserUpdate(roomID, updateType, userList)
                }


            }



            zegoEngine.setEventHandler(eventhandler)
        }

    }
