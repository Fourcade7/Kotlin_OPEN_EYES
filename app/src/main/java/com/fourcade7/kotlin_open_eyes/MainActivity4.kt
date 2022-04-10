package com.fourcade7.kotlin_open_eyes

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.util.Log

import android.webkit.PermissionRequest

import android.webkit.WebChromeClient
import kotlinx.android.synthetic.main.activity_main4.*
import android.webkit.WebView

import android.webkit.WebViewClient
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CallSettings.CallSettingsBuilder
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User
import java.security.AccessController.getContext
import java.util.*


class MainActivity4 : AppCompatActivity() {

     var  appID = "2067761cecf1ddb0"
     var  region = "us"
     var  auth_KEY = "9d6a42e6bb941cc0c404e5878786c943b89e17e2"

     val TAG = "Pr"
     val UID = "superhero1"
    //for call
    val sessionID: String = "SESSION_ID"
    val receiverID:String=UID
    val receiverType:String = CometChatConstants.RECEIVER_TYPE_USER
    val callType:String = CometChatConstants.CALL_TYPE_VIDEO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        setTitle("Video Call")










    }







}