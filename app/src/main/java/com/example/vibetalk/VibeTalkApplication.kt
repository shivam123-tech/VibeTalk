package com.example.vibetalk
import android.app.Application
import com.example.vibetalk.rtc.RTCManager
import im.zego.zegoexpress.ZegoExpressEngine
import im.zego.zegoexpress.constants.ZegoScenario
import java.util.UUID

class VibeTalkApplication : Application() {

    lateinit var zegoEngine : ZegoExpressEngine
    lateinit var rtcManager: RTCManager

    override fun onCreate() {
        super.onCreate()

        zegoEngine = ZegoExpressEngine.createEngine(
            189447505,
            "1fde1d7b9056fee612b72f3c145772a562c75007ed42d19b23555a7d349289b1",
            false,
            ZegoScenario.GENERAL,
            this,
            null
        )

        rtcManager = RTCManager(zegoEngine)

        val sharedPreference = getSharedPreferences("USER",MODE_PRIVATE);
        val editor = sharedPreference.edit();
      val check=  sharedPreference.getString("UserId","");
        if(check==""){
            val userID = UUID.randomUUID().toString()
            editor.putString("UserId",userID);
            editor.apply()
        }
    }

}