package com.example.tijiosdktest.audio

import android.content.Context
import android.media.*
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Modifier
import java.nio.ByteBuffer

class AudioUtils {

    /**
     * 两个视频 合成 一个视频
     */
    fun videoTwo(context: Context, videoPath: String, twoVideo: String, outPath: String) {
        val mediaMuxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val videoExtractor1 = MediaExtractor()
        videoExtractor1.setDataSource(videoPath)
        val videoTractor1 = selectTract(videoExtractor1, false)
        val audioTractor1 = selectTract(videoExtractor1, true)
        val videoFormat = videoExtractor1.getTrackFormat(videoTractor1)
        val audioFormat = videoExtractor1.getTrackFormat(audioTractor1)
        videoExtractor1.selectTrack(videoTractor1)
        val sourceVideo = mediaMuxer.addTrack(videoFormat)
        val sourceAudio = mediaMuxer.addTrack(audioFormat)
        mediaMuxer.start()

        writeMuxerData(mediaMuxer,videoExtractor1,videoFormat,sourceVideo,0)
        videoExtractor1.unselectTrack(videoTractor1)
        videoExtractor1.selectTrack(audioTractor1)
        writeMuxerData(mediaMuxer,videoExtractor1,audioFormat,sourceAudio,0)

        val videoExtractor2 = MediaExtractor()
        videoExtractor2.setDataSource(twoVideo)
        val duration = videoFormat.getLong(MediaFormat.KEY_DURATION)

        val videoTractor2 = selectTract(videoExtractor2, false)
        val audioTractor2 = selectTract(videoExtractor2, true)
        videoExtractor2.selectTrack(videoTractor2)
        writeMuxerData(mediaMuxer,videoExtractor2,videoExtractor2.getTrackFormat(videoTractor2),sourceVideo,duration)
        videoExtractor2.unselectTrack(videoTractor2)
        videoExtractor2.selectTrack(audioTractor2)
        writeMuxerData(mediaMuxer,videoExtractor2,videoExtractor2.getTrackFormat(audioTractor2),sourceAudio,duration)

    }

    private fun writeMuxerData(muxer: MediaMuxer,extractor: MediaExtractor,format: MediaFormat,sourceIndex: Int,duration: Long) {
        val info = MediaCodec.BufferInfo()
        info.offset = 0
        val buffer = if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) ByteBuffer.allocateDirect(format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE))
                        else ByteBuffer.allocateDirect(100*1024)
        while (true) {
            val sampleTime = extractor.sampleTime
            if (sampleTime < 0) break

            info.size = extractor.readSampleData(buffer, 0)
            info.presentationTimeUs = duration + sampleTime
            info.flags = extractor.sampleFlags

            muxer.writeSampleData(sourceIndex,buffer,info)

            extractor.advance()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun mixVideo(context: Context, videoPath: String, audioPath: String, startTime: Long, endTime: Long, volume2: Int, volume1: Int) {
        val mixPcmPath = File(context.externalCacheDir,"output.pcm").absolutePath
        mixPcmAudio(context,videoPath,audioPath,mixPcmPath,startTime,endTime,volume1,volume2)

        val tempWavPath = File(context.externalCacheDir,"temp.wav").absolutePath
        val outputPath = File(context.externalCacheDir,"ot.mp4").absolutePath
        clipVideo(videoPath,tempWavPath,outputPath,startTime, endTime)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun clipVideo(videoPath: String,audioPath: String,outPath: String,startTime: Long,endTime: Long) {  // 剪辑视频
        val mediaMuxer = MediaMuxer(outPath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)  // mp4 格式输出

        val mediaExtractor = MediaExtractor() // 解封装
        mediaExtractor.setDataSource(videoPath)  // 设置数据源
        val videoTract = selectTract(mediaExtractor, false)
        val videoFormat = mediaExtractor.getTrackFormat(videoTract)

        val muxerVideoTract = mediaMuxer.addTrack(videoFormat) // 添加视频轨

        val audioTract = selectTract(mediaExtractor, true)
        val audioFormat = mediaExtractor.getTrackFormat(audioTract)
        audioFormat.setString(MediaFormat.KEY_MIME,MediaFormat.MIMETYPE_AUDIO_AAC)
        val audioBitRate = audioFormat.getInteger(MediaFormat.KEY_BIT_RATE)

        val muxerAudioTract = mediaMuxer.addTrack(audioFormat)  // 添加音频轨

        mediaMuxer.start() // 开始输出工作

        val info = MediaCodec.BufferInfo()
        // 输入音频轨信息
        val pcmExtractor = MediaExtractor()  // 合成后的音频信息
        pcmExtractor.setDataSource(audioPath)
        val pcmTractor = selectTract(pcmExtractor,true)
        pcmExtractor.selectTrack(pcmTractor)
        val pcmFormat = pcmExtractor.getTrackFormat(pcmTractor)

        val maxBufferSize = if (pcmFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) pcmFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) else 100 * 1000

        val aacMediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC,44100,2)
        aacMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,audioBitRate)
        aacMediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        aacMediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,maxBufferSize)
        val aacCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)  // 音频编码 aac 后 输入
        aacCodec.configure(aacMediaFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE)
        aacCodec.start()

        val audioBuffer = ByteBuffer.allocateDirect(maxBufferSize)
        var isAudioEncode = false
        while (!isAudioEncode) {
            val inputIndex = aacCodec.dequeueInputBuffer(10000)
            if (inputIndex >= 0) {
                val sampleTime = pcmExtractor.sampleTime
                if (sampleTime < 0){
                    aacCodec.queueInputBuffer(inputIndex,0,0,0,MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                } else {
                    val flags = pcmExtractor.sampleFlags
                    val size = pcmExtractor.readSampleData(audioBuffer,0)
                    val inputBuffer = aacCodec.getInputBuffer(inputIndex)
                    inputBuffer?.clear()
                    inputBuffer?.put(audioBuffer)
                    inputBuffer?.position(0)

                    aacCodec.queueInputBuffer(inputIndex,0,size,sampleTime,flags)
                    pcmExtractor.advance()
                }
            }

            var outputIndex = aacCodec.dequeueOutputBuffer(info, 1000)
            while (outputIndex >= 0) {
                if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    isAudioEncode = true
                    break
                }
                val outputBuffer = aacCodec.getOutputBuffer(outputIndex) ?: continue
                mediaMuxer.writeSampleData(muxerAudioTract,outputBuffer,info)
                outputBuffer.clear()
                aacCodec.releaseOutputBuffer(outputIndex,false)
                outputIndex = aacCodec.dequeueOutputBuffer(info,1000)
            }
        }

        // 输入视频轨信息
        mediaExtractor.unselectTrack(audioTract)
        mediaExtractor.selectTrack(videoTract)
        mediaExtractor.seekTo(startTime,MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        val bufferSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        val buffer = ByteBuffer.allocateDirect(bufferSize)
        while (true) {
            val sampleTime = mediaExtractor.sampleTime
            if (sampleTime == -1L) break
            if (sampleTime < startTime) {
                mediaExtractor.advance()
                continue
            }
            if (sampleTime > endTime) break
            info.presentationTimeUs = sampleTime - startTime + 600
            info.flags = mediaExtractor.sampleFlags
            info.size = mediaExtractor.readSampleData(buffer,0)
            if (info.size < 0) break
            mediaMuxer.writeSampleData(muxerVideoTract,buffer,info)
            mediaExtractor.advance()
        }

        mediaExtractor.release()
        pcmExtractor.release()
        mediaMuxer.release()
        aacCodec.stop()
        aacCodec.release()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun mixPcmAudio(context: Context, videoPath: String, audioPath: String, outputPath: String, startTime: Long, endTime: Long, volume1: Int, volume2: Int) {
        val vol1 : Float = volume1 / 100f * 1
        val vol2 = volume2 / 100f * 1
        val videoPcmPath = File(context.externalCacheDir,"video.pcm").absolutePath
        val audioPcmPath = File(context.externalCacheDir,"audio.pcm").absolutePath

        decodeToPcm(videoPath,videoPcmPath,startTime,endTime)  // 输出视频 中 的音频信息

        decodeToPcm(audioPath,audioPcmPath,startTime, endTime) // 输出音频 中 的pcm数据

        // 读取 两个pcm文件 波形叠加 形成新的pcm文件

        val videoFis = FileInputStream(videoPcmPath)
        val audioFis = FileInputStream(audioPcmPath)
        val tempFos = FileOutputStream(outputPath)

        val videoBuffer = ByteArray(2048)
        val audioBuffer = ByteArray(2048)
        val tempBuffer = ByteArray(2048)

        var isEnd1 = false
        var isEnd2 = false
        while (!isEnd1 || !isEnd2) {
            if (!isEnd1) {
                isEnd1 = videoFis.read(videoBuffer) == -1
            }

            if (!isEnd2) {
                isEnd2 = audioFis.read(audioBuffer) == -1

                for (i in audioBuffer.indices step 2) { // 前面是低字节 后面是高字节  两个音频数据都是双通道格式(未适配 需要重采样)
                    val videoVolume = ((videoBuffer[i].toInt() and 0xFF) or ((videoBuffer[i+1].toInt() and 0xFF) shl 8)).toShort()
                    val audioVolume = ((audioBuffer[i].toInt() and 0xFF) or ((audioBuffer[i+1].toInt() and 0xFF) shl 8)).toShort()

                    var tempVolume = (videoVolume * vol1 + audioVolume * vol2).toInt()
                    if (tempVolume > Short.MAX_VALUE) {  // 最多两个字节  此处需要等比降低
                        tempVolume = Short.MAX_VALUE.toInt()
                    } else if (tempVolume < Short.MIN_VALUE) {
                        tempVolume = Short.MIN_VALUE.toInt()
                    }

                    tempBuffer[i] = (tempVolume and 0xFF).toByte()
                    tempBuffer[i + 1] = ((tempVolume shr 8) and 0xFF).toByte()
                }

                tempFos.write(tempBuffer)
            }
        }

        videoFis.close()
        audioFis.close()
        tempFos.close()

        val tempPcmPath = File(context.externalCacheDir,"temp.wav").absolutePath
        PcmToWavUtils.toWav(44100,AudioFormat.CHANNEL_IN_STEREO,2,AudioFormat.ENCODING_PCM_16BIT, outputPath,tempPcmPath)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun decodeToPcm(path: String,outPath: String, startTime: Long, endTime: Long) {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(path)

        val selectTract = selectTract(mediaExtractor)
        mediaExtractor.selectTrack(selectTract)
        val trackFormat = mediaExtractor.getTrackFormat(selectTract)

        mediaExtractor.seekTo(startTime,MediaExtractor.SEEK_TO_CLOSEST_SYNC)

        val mediaCodec = MediaCodec.createDecoderByType(trackFormat.getString(MediaFormat.KEY_MIME))
        mediaCodec.configure(trackFormat,null,null,0)

        val buffer = if (trackFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            val bufferSize = trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            ByteBuffer.allocateDirect(bufferSize)
        } else {
            ByteBuffer.allocateDirect(100_0000)
        }

        val channel = FileOutputStream(File(outPath)).channel
        mediaCodec.start()

        val info = MediaCodec.BufferInfo()
        while (true) {
            if (mediaExtractor.sampleTime == -1L) break
            if (mediaExtractor.sampleTime < startTime) {
                mediaExtractor.advance()
                continue
            }
            if (mediaExtractor.sampleTime > endTime) break

            info.size = mediaExtractor.readSampleData(buffer, 0)
            info.presentationTimeUs = mediaExtractor.sampleTime
            info.flags = mediaExtractor.sampleFlags

            val content = ByteArray(buffer.remaining())
            buffer.get(content)

            val inIndex = mediaCodec.dequeueInputBuffer(10_000)
            if (inIndex >= 0) {
                val inputBuffer = mediaCodec.getInputBuffer(inIndex)
                inputBuffer?.clear()
                inputBuffer?.put(content)
                mediaCodec.queueInputBuffer(inIndex,0,info.size,info.presentationTimeUs,info.flags)
                mediaExtractor.advance()
            }

            var outIndex = mediaCodec.dequeueOutputBuffer(info, 1000)
            while (outIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outIndex)
                //write pcm file
                channel.write(outputBuffer)
                mediaCodec.releaseOutputBuffer(outIndex,false)
                outIndex = mediaCodec.dequeueOutputBuffer(info,1000)
            }

        }

        channel.close()
        mediaCodec.stop()
        mediaExtractor.release()
        mediaCodec.release()

    }

    private fun selectTract(mediaExtractor: MediaExtractor,isAudio: Boolean = true) : Int {
        for (i in 0 until mediaExtractor.trackCount) {
            val trackFormat = mediaExtractor.getTrackFormat(i)
            if (isAudio && trackFormat.getString(MediaFormat.KEY_MIME)?.contains("audio",true) == true) {
                return i
            }
            if (!isAudio && trackFormat.getString(MediaFormat.KEY_MIME)?.contains("video",true) == true) {
                return i
            }
        }
        return -1
    }
}