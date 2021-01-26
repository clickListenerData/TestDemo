package com.example.tijiosdktest

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class H264Encode(private val context: Context) : Runnable,Camera.PreviewCallback {


    private val mediaCodec = MediaCodec.createEncoderByType("video/hevc")
    @Volatile private var isStart = false

    fun init() {
        val format = MediaFormat.createVideoFormat("video/hevc", 1280, 720)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 720 * 1280)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    fun startEncode() {
        init()
        isStart = true
        mediaCodec.start()
    }

    fun stopEncode() {
        isStart = false
    }

    override fun run() {

    }

    private fun setData(data: ByteArray) {
        val inIndex = mediaCodec.dequeueInputBuffer(100_000)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && inIndex >= 0) {
            val inputBuffer = mediaCodec.getInputBuffer(inIndex)
            inputBuffer?.clear()
            inputBuffer?.put(data)
            mediaCodec.queueInputBuffer(inIndex, 0, data.size, 0, 0)

            val file = File(context.cacheDir.absolutePath + File.separator + "test.h265")
            val fos = FileOutputStream(file, true)
            val info = MediaCodec.BufferInfo()
            val outIndex = mediaCodec.dequeueOutputBuffer(info, 100_000)
            if (outIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outIndex)
                val newData = ByteArray(outputBuffer!!.remaining())
                outputBuffer.get(newData)
                fos.write(newData)
                fos.flush()
                fos.close()
                mediaCodec.releaseOutputBuffer(outIndex, false)
            }
        }
    }

    private fun nv21toNV12(data: ByteArray, width: Int, height: Int) : ByteArray {
        var tmp : Byte
        val len = height * 3 / 2  // u v 数据   data 总大小 width * height * 3 / 2
        for (i in 0 until width step 2) {
            for (j in height until len) {
                val index = j * width + i
                tmp = data[index]
                data[index] = data[index + 1]
                data[index + 1] = tmp
            }
        }
        return data
    }

    // 数据需要旋转90°  变为正的
    private fun dataOritention(data: ByteArray, width: Int, height: Int): ByteArray {
        val newBuffer = ByteArray(data.size)
        var k = 0
        for (i in 0 until width) {
            for (j in height - 1..0) {
                val index = j * width + i
                newBuffer[k++] = data[index]
            }
        }

        val count = width*height
        for (i in 0 until width step 2) {
            for (j in (height/2-1)..0) {
                val index = j * width + i
                newBuffer[k++] = data[count + index]
                newBuffer[k++] = data[count + index + 1]
            }
        }

        return newBuffer
// 4 * 4
       /* for (i in 0 until width) {
            for (j in 0 until height) {
                // 新数组中摆放Y值 旋转后(i,j) --> 旋转前(srcHeight-1-j, i)
                newBuffer[i * height + j] = data[(height - 1 - j) * width + i]
                // 确认是左上角的点
                if (i % 2 == 0 && j % 2 == 0) {
                    // 摆放V值 目标行号= 行号/2 + 高
                    newBuffer[(i / 2 + width) * height + j] = data[((height - 1 - j) / 2 + height) * width + j]
                    // 摆放U值
                    newBuffer[(i / 2 + width) * height + j + 1] = data[((height - 1 - j) / 2 + height) * width + j + 1]
                }
            }
        }

        return newBuffer*/
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        val width = camera?.parameters?.previewSize?.width ?: 0
        val height = camera?.parameters?.previewSize?.height ?: 0
        if (data != null && isStart) {
//            val buffer = dataOritention(nv21toNV12(data, width, height), width, height)
            setData(nv21toNV12(data, width, height))
//            captrue(buffer, width, height)
//            isStart = false
        }
    }

    fun captrue(temp: ByteArray?, width: Int, height: Int) {

        //保存一张照片
        val fileName = "IMG_" + 1.toString() + ".jpg" //jpeg文件名定义
        val pictureFile = File(context.cacheDir.absolutePath + File.separator + fileName)
        if (!pictureFile.exists()) {
            try {
                pictureFile.createNewFile()
                val filecon = FileOutputStream(pictureFile)
                //ImageFormat.NV21 and ImageFormat.YUY2 for now
                val image = YuvImage(temp, ImageFormat.NV21, height, width, null) //将NV21 data保存成YuvImage
                //图像压缩
                image.compressToJpeg(
                        Rect(0, 0, image.width, image.height),
                        100, filecon) // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}