package com.example.tijiosdktest.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.math.abs

class Camera2Help {

    private lateinit var mPreviewSize: Size

    private lateinit var mBackgroundThread: HandlerThread
    private lateinit var mHandler : Handler

    private lateinit var mSession: CameraCaptureSession

    private var cameraDevice: CameraDevice? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun start(context: Context, open: (device: CameraDevice) -> Unit) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val backCameraId = getBackCameraId(cameraManager) ?: return
        if (!::mPreviewSize.isInitialized) return
        mBackgroundThread = HandlerThread("cameraBackGround") // camera 回调的线程
        mBackgroundThread.start()
        mHandler = Handler(mBackgroundThread.looper)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        cameraManager.openCamera(backCameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                open(camera)
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
                cameraDevice = null
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
                cameraDevice = null
            }

        }, mHandler)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openCamera(context: Context, textureView: TextureView) {
        start(context) {
            startPreview(textureView, getImageReader(), false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startPreview(textureView: TextureView, imageReader: ImageReader?, isFlash: Boolean) {
        if (cameraDevice == null) return
        val surfaceTexture = textureView.surfaceTexture
        surfaceTexture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
        val surface = Surface(surfaceTexture)

        val requestBuild = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        requestBuild.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        )
        if (isFlash) requestBuild.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
        requestBuild.addTarget(surface)
        if (imageReader != null) requestBuild.addTarget(imageReader.surface)

        if (::mSession.isInitialized) {
            mSession.stopRepeating()
            mSession.abortCaptures()
            mSession.setRepeatingRequest(
                requestBuild.build(),
                object : CameraCaptureSession.CaptureCallback() {},
                mHandler
            )
            return
        }
        cameraDevice!!.createCaptureSession(
            arrayListOf(surface,imageReader?.surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    session.setRepeatingRequest(
                        requestBuild.build(),
                        object : CameraCaptureSession.CaptureCallback() {},
                        mHandler
                    )
                    mSession = session
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {

                }

            }, mHandler
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getImageReader() : ImageReader {
        val imReader = ImageReader.newInstance(
            mPreviewSize.width,
            mPreviewSize.height,
            ImageFormat.YUV_420_888,
            2
        )
        imReader.setOnImageAvailableListener({
            val image = it.acquireNextImage()
            Log.i("zzzzzzzzzzzzzz","${image.format} ,, ${image.width} ,, ${image.height}")
            //            搞事情           image 内容转换成
//           yuv  H264
            if (image.format == ImageFormat.YUV_420_888) {
                // 此时得到的planes 是 YUV 三个buffer分量
            } else if (image.format == ImageFormat.JPEG) {
                // planes[0] 一个图片数据
            }
            val planes = image.planes
            // 重复使用同一批byte数组，减少gc频率
//            if (y == null) {
////                new  了一次
////                limit  是 缓冲区 所有的大小     position 起始大小
//                y = ByteArray(planes[0].buffer.limit() - planes[0].buffer.position())
//                u = ByteArray(planes[1].buffer.limit() - planes[1].buffer.position())
//                v = ByteArray(planes[2].buffer.limit() - planes[2].buffer.position())
//            }
//            if (image.planes[0].buffer.remaining() == y.size) {
////                分别填到 yuv
//                planes[0].buffer.get(y)
//                planes[1].buffer.get(u)
//                planes[2].buffer.get(v)
////                yuv 420
//            }
            image.close()
        }, mHandler)
        return imReader
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBackCameraId(manager: CameraManager) : String? {
        manager.cameraIdList.forEach {
            val cameraCharacteristics = manager.getCameraCharacteristics(it)
            val facing = cameraCharacteristics[CameraCharacteristics.LENS_FACING]  // 前置 后置 外部
            val ints = cameraCharacteristics[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES]  // 支持的功能集合  LOGICAL_MULTI_CAMERA 逻辑相机 可能包含多个物理相机
            val map = cameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]
            Log.i("zzzzzzzzzzzzzzzzzz", Arrays.toString(ints) + "::::" + facing + "::")
            if (facing == CameraCharacteristics.LENS_FACING_BACK && ints?.contains(
                    CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
                ) == true && map != null) {
                val outputSizes = map.getOutputSizes(SurfaceTexture::class.java)  // 支持的预览尺寸
                mPreviewSize = getBestSupportedSize(outputSizes, null)  // 最合适的预览尺寸
                return it
            }
        }

        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBestSupportedSize(sizes: Array<Size>, previewSize: Point?) : Size {
        val maxPreViewSize = Point(1920, 1080)
        val minPreViewSize = Point(1280, 720)

        val defaultSize = sizes[0]
        Arrays.sort(sizes, Comparator { o1, o2 ->
            return@Comparator if (o1.width > o2.width) -1 else if (o1.width == o2.width) if (o1.height > o2.height) -1 else 1 else 1
        })
        val newSizes = sizes.filter {
            it.width in minPreViewSize.x..maxPreViewSize.x && it.height in minPreViewSize.y..maxPreViewSize.y
        }
        if (newSizes.isEmpty()) return defaultSize
        var bestSize = newSizes[0]
        var previewRatio = if (previewSize != null) previewSize.x / previewSize.y else bestSize.width / bestSize.height
        if (previewRatio > 1) previewRatio = 1 / previewRatio
        newSizes.forEach {
            val bestRatio = bestSize.height / bestSize.width
            val ratio = it.height / it.width
            if (abs(ratio - previewRatio) < abs(bestRatio - previewRatio)) {
                bestSize = it
            }
        }
        return bestSize
    }
}