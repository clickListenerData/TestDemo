package com.example.tijiosdktest.camera

import android.graphics.ImageFormat
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class CameraXHelp {

    private var y : ByteArray? = null
    private var u : ByteArray? = null
    private var v : ByteArray? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun start(activity: AppCompatActivity, preview: PreviewView) {
        val provider = ProcessCameraProvider.getInstance(activity)

        provider.addListener(Runnable {
            val cameraProvider = provider.get()
            val previewBuild = Preview.Builder()
                .build()
            val camera = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            previewBuild.setSurfaceProvider(preview.surfaceProvider)

            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1080,1920))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)  // 非阻塞模式
                .build()

            analysis.setAnalyzer(Executors.newSingleThreadExecutor(), {

                ImageFormat.YUV_420_888
                if (y == null) {
                    val y_len = it.planes[0].buffer.limit() - it.planes[0].buffer.position()
                    val u_len = it.planes[1].buffer.limit() - it.planes[1].buffer.position()
                    val v_len = it.planes[2].buffer.limit() - it.planes[2].buffer.position()

                    y = ByteArray(y_len)
                    u = ByteArray(u_len)
                    v = ByteArray(v_len)
                }

                it.planes[0].buffer.get(y)
                it.planes[1].buffer.get(u)
                it.planes[2].buffer.get(v)

//                val nv21Size = y!!.size + u!!.size / 2 + v!!.size / 2   // uv  一个像素点为2  n v index+=2 跳着赋值
//                ImageUtils.yuvToNV12(y!!,u!!,v!!, byteArrayOf(),it.planes[0].rowStride,it.height)

                Log.i(
                    "zzzzzzzzzzzzzzzz",
                    "${it.format} ,, ${it.planes.size} ${it.planes[0].rowStride} ${it.width} ${it.height}"
                )  // YUV
                it.close()
            })

            val capture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_ON)
                .setTargetRotation(preview.display.rotation)
                .build()


            cameraProvider.bindToLifecycle(activity, camera, capture, analysis, previewBuild)

            /*capture.takePicture(ContextCompat.getMainExecutor(activity), object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    Log.i("zzzzzzzzzzzzzzz","${image.format}")  // JPEG
                }
            })*/
//            capture.takePicture()
        }, ContextCompat.getMainExecutor(activity))
    }
}