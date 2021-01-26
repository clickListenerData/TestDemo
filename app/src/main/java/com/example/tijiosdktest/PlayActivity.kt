package com.example.tijiosdktest

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity(), SurfaceHolder.Callback {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        sfv.holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (holder == null) return
        val player = H264Player(this,holder.surface)
        player.play()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }
}