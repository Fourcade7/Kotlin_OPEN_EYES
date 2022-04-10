package com.fourcade7.kotlin_open_eyes

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity3 : AppCompatActivity() {
    lateinit var mediaPlayercamera: MediaPlayer
    lateinit var mediaPlayerred: MediaPlayer
    lateinit var mediaPlayegreen: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        mediaPlayercamera=MediaPlayer.create(this@MainActivity3, R.raw.camera_1)
        mediaPlayerred=MediaPlayer.create(this@MainActivity3, R.raw.camera_2)
        mediaPlayegreen=MediaPlayer.create(this@MainActivity3, R.raw.camera_3)



        GlobalScope.launch(Dispatchers.Main) {
            if (!mediaPlayercamera.isPlaying) {
                mediaPlayercamera.start()
                delay(7000)
                mediaPlayerred.start()

            }

        }
    }
}