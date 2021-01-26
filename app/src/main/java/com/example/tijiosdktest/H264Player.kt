package com.example.tijiosdktest

import android.content.Context
import android.graphics.*
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.io.*
import java.lang.Exception

/**
 * H264 文件解析播放  将MediaCodec 改为hevc 编码 可播放H265文件
 *
 */
class H264Player(private val context: Context,surface: Surface) : Runnable {

    companion object {
        // 分隔符
        const val START_INDEX_ONE : Byte = 0X00
        const val START_INDEX_FOUR : Byte = 0X01
    }

    private val mediaCodec = MediaCodec.createDecoderByType("video/hevc")

    init {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC,1280,720)
        format.setInteger(MediaFormat.KEY_FRAME_RATE,15)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,2)
        format.setInteger(MediaFormat.KEY_BIT_RATE,720*1280)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
        mediaCodec.configure(format,surface,null,0)
    }

    fun play() {
        mediaCodec.start()
        Thread(this).start()
    }

    override fun run() {
        try {
            decodeH264()
        }catch (e: Exception) {

        }
    }

    var i = 0
    private fun decodeH264() {
        val bytes = getH264Bytes()

        var startIndex = 0
        var nextFrameIndex = 0
        val info = MediaCodec.BufferInfo()

        while (true) {
            // 是否有输入buffer可用
            val inIndex = mediaCodec.dequeueInputBuffer(100_000)
            if (inIndex >= 0) {
                // 2 ： 跳过得字节数
                nextFrameIndex = findNextIndex(bytes,startIndex + 2)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val inputBuffer = mediaCodec.getInputBuffer(inIndex)
                    inputBuffer?.clear()
                    inputBuffer?.put(bytes,startIndex,nextFrameIndex - startIndex)
                    mediaCodec.queueInputBuffer(inIndex,0,nextFrameIndex - startIndex,0,0) // 通知 mediacodec buffer 有数据
                    Log.i("zzzzzzzzzzzzzzzzzzz","$startIndex ,, $nextFrameIndex")
                    startIndex = nextFrameIndex  // 准备下一帧
                }

                val outIndex = mediaCodec.dequeueOutputBuffer(info, 100_000)  // 10ms内解码完成 返回
                if (outIndex >= 0) {
                    Thread.sleep(15)  // 帧率  应该是 等待时间 + 解码时间 + 渲染时间
                    /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        val outputBuffer = mediaCodec.getOutputBuffer(outIndex)
                        outputBuffer?.position(info.offset)
                        outputBuffer?.limit(info.offset + info.size)  // 限制数据大小 即后面的空数据不需要
                        val data = ByteArray(outputBuffer!!.remaining())
                        outputBuffer.get(data)  // 获取 当前帧的 yuv 数据

                        val image = YuvImage(data,ImageFormat.NV21,1280,720,null)

                        val bos = ByteArrayOutputStream()
                        image.compressToJpeg(Rect(0,0,1280,720),100,bos)
                        val bitmapData = bos.toByteArray()
                        val bitmap = BitmapFactory.decodeByteArray(bitmapData,0,bitmapData.size)

                        if (bitmap != null) {
                            val file = File(context.cacheDir.path + File.separator + "image_$i.png")
                            val bufferOS = BufferedOutputStream(FileOutputStream(file))
                            bitmap.compress(Bitmap.CompressFormat.PNG,100,bufferOS)
                            bufferOS.flush()
                            bufferOS.close()
                        }

                        i++

                    }*/
                    mediaCodec.releaseOutputBuffer(outIndex,true)  // 传递true 渲染至缓冲区surface  false 不渲染surface 上面surface传null

                }
            }
        }
    }

    private fun getH264Bytes() : ByteArray { // 获取H264 的 码流数据
        val openIs = FileInputStream(File(context.cacheDir.absolutePath + File.separator + "test.h265"))
        val bytes =  openIs.readBytes()
        openIs.close()
        return bytes
//        return context.assets.open("out.h264").readBytes()
    }

    private fun findNextIndex(bytes: ByteArray,index: Int) : Int { // 获取 下一帧分隔符 所在位置
        for (i in index until bytes.size - 3) {
            if (bytes[i] == START_INDEX_ONE && bytes[i + 1] == START_INDEX_ONE && bytes[i + 2] == START_INDEX_ONE && bytes[i + 3] == START_INDEX_FOUR) {
                return i
            }
            /*if (bytes[i] == START_INDEX_ONE && bytes[i + 1] == START_INDEX_ONE && bytes[i + 2] == START_INDEX_FOUR) {
                return i
            }*/
        }
        return -1
    }
}