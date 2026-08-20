package com.example.vibetalk

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import im.zego.zegoexpress.entity.ZegoCanvas
import kotlinx.coroutines.launch
import android.util.Log
import android.view.Gravity
import android.view.TextureView
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import im.zego.zegoexpress.constants.ZegoViewMode


class MainActivity : AppCompatActivity() {
    lateinit var app : VibeTalkApplication
    lateinit var canvas : ZegoCanvas;

    var cameraFlag = true

    val permissionLaunncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
            result ->
        val cameraGranted = result[Manifest.permission.CAMERA] == true
        val audioGranted = result[Manifest.permission.RECORD_AUDIO] == true

        //opening shared perefrence
        val sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        val userId = sharedPreferences.getString("UserId","");

        if(cameraGranted && audioGranted){

            app.zegoEngine.enableCamera(true)
            app.zegoEngine.startPreview(canvas)

            app.rtcManager.loginRoom(
                roomId = "room123",
                 userId = userId,
                  userName = "${userId}A"
            ){

                app.rtcManager.publishStream("${userId}camera")
            }
        } else{
            Toast.makeText(this,"PERMISSION IS DENIED", Toast.LENGTH_SHORT).show()

        }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //TO KEEP SCREEN AWAKE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cameraPreview = findViewById<TextureView>(R.id.frame);
        canvas = ZegoCanvas(cameraPreview);
        canvas.viewMode = ZegoViewMode.ASPECT_FILL

        //using another container for black screen for camera off
        val localVideoContainer= findViewById<FrameLayout>(R.id.localVideoContainer);
        val params = localVideoContainer.layoutParams as FrameLayout.LayoutParams


        val callButton = findViewById< ImageButton>(R.id.callendButton);

        app = application as VibeTalkApplication

        val rtcManager = app.rtcManager;
        rtcManager.listOfStream()
        val factory = CallViewModelFactory(rtcManager)
        val viewModel = ViewModelProvider(this,factory)[CallViewModel::class.java];

        //observing mutable state flow and showing remote stream
        val remoteSurface = findViewById<TextureView>(R.id.remote)
       val remoteCanvas = ZegoCanvas(remoteSurface);
        remoteCanvas.viewMode = ZegoViewMode.ASPECT_FILL

        val remoteCameraOff = findViewById<FrameLayout>(R.id.remoteCameraOffView);


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.uiState.collect { state ->


                        if (state.showRemoteVideo) {
                            remoteSurface.visibility = View.VISIBLE

                            rtcManager.startPlayingStream(
                                state.remoteStreamId,
                                remoteCanvas
                            )
                            params.height = 420
                            params.width = 330

                            params.gravity = Gravity.TOP or Gravity.END
                            params.rightMargin = 50
                            params.topMargin = 50

                            localVideoContainer.layoutParams = params

                        } else {
                            remoteSurface.visibility = View.GONE

                            params.width = FrameLayout.LayoutParams.MATCH_PARENT
                            params.height = FrameLayout.LayoutParams.MATCH_PARENT

                            params.gravity = Gravity.NO_GRAVITY

                            params.rightMargin = 0
                            params.bottomMargin = 0

                            localVideoContainer.layoutParams = params
                            //here we not calling .stopPlayingMethod because in RTCManager when calling delete method there we make streamID="" (empty) then how can we stop stream here using streamID
                            //so we have called .stopPlayingStream in RTCManager only before make stramID=""
                        }


                        //remoteCameraState
                        if (state.remoteCameraState) {
                            remoteCameraOff.visibility = View.GONE
                        } else {
                            remoteCameraOff.visibility = View.VISIBLE
                        }


                    }
                }

                launch {
                    viewModel.remoteCallEnded.collect {
                        finish();
                    }
                }
            }
        }


     //END CALL BUTTON LOGIC
        callButton.setOnClickListener {
              rtcManager.callEnd(
                  roomId = "room123"
              )
               finish()
        }

        //MUTE MIC
        var micFlag = false
        val Mic = findViewById<ImageButton>(R.id.muteButton);
        Mic.setOnClickListener {
            micFlag = !micFlag
            rtcManager.muteMic(micFlag)

            if (micFlag) {
                Mic.setImageResource(R.drawable.ic_mutemic);
            } else {
                Mic.setImageResource(R.drawable.ic_mic);
            }
        }

        //CAMERA OFF
        val camera = findViewById<ImageButton>(R.id.cameraButton);
        val blackCameraView = findViewById<View>(R.id.blackCameraView);
        camera.setOnClickListener {
            cameraFlag = !cameraFlag
            rtcManager.cameraOff(cameraFlag);

            if(cameraFlag){
                camera.setImageResource(R.drawable.ic_camera)
                blackCameraView.visibility = View.GONE
            }else{
                camera.setImageResource(R.drawable.ic_cameraoff)
                blackCameraView.visibility = View.VISIBLE

            }
        }

        //switchCamera
        val switchCamera = findViewById<ImageButton>(R.id.switchCamera);
        var switchCameraFlag = true;
        switchCamera.setOnClickListener {
            switchCameraFlag = !switchCameraFlag
            rtcManager.switchCamera(switchCameraFlag);
            if(switchCameraFlag){
                switchCamera.setImageResource(R.drawable.camera_switch);
            }else{
                switchCamera.setImageResource(R.drawable.camera_switchhhh);

            }
        }




        permissionLaunncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )


    }

    override fun onStop() {
        super.onStop()
        app.rtcManager.cameraOff(false)
    }

    override fun onStart() {
        super.onStart()
        app.rtcManager.cameraOff(cameraFlag)

    }

}