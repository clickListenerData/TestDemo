package com.example.tijiosdktest

import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_encode.*
import kotlin.concurrent.thread

class EncodeActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private val encode by lazy { H264Encode(this) }

    private lateinit var holder: SurfaceHolder

    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode)

        sfv.holder.addCallback(this)

        btn_start.setOnClickListener {
            encode.startEncode()
        }

        btn_stop.setOnClickListener {
            encode.stopEncode()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        this.holder = holder!!
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),100)
        } else {
            openCamera()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        this.holder = holder!!
        if (::camera.isInitialized) camera.setPreviewDisplay(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    private fun openCamera() {
        thread {
            camera = Camera.open()

            val parameters = camera.parameters
//            parameters.previewFormat = ImageFormat.NV21
            parameters.setPreviewSize(1280,720)
            /*parameters.supportedPreviewSizes.forEach {
                Log.i("zzzzzzzzzzzzzzzzzzz","${it.width} .. ${it.height}")
            }*/
            camera.setDisplayOrientation(90)
            camera.parameters = parameters

            camera.setPreviewDisplay(holder)
            camera.setPreviewCallback(encode)
            camera.startPreview()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }
    }


}