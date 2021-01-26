package com.example.tijiosdktest.camera

object ImageUtils {

    // NV12  U在前V在后   ////  NV21 V在前Please fix the following before submitting a JCenter inclusion request:
    //- Add a POM file to the latest version of your package.
    fun yuvToNV12(
        y: ByteArray,
        u: ByteArray,
        v: ByteArray,
        nv12: ByteArray,
        stride: Int,
        height: Int
    ) {
        val size = y.size + u.size / 2 + v.size / 2
        if (nv12.size != size) return
        System.arraycopy(y, 0, nv12, 0, y.size)

        var index = 0
        for (i in (stride * height) until size step 2) {
            nv12[i] = u[index]
            nv12[i + 1] = v[index]
            index += 2
        }
    }

    // 图片 数据 旋转90
    fun nv12Rotate90(nv12: ByteArray, nv12Rotate: ByteArray, width: Int, height: Int) {
        val ySize = width * height
        val bufferSize = ySize * 3 / 2

        var k = 0
        val startPos = (height - 1) * width
        for (i in 0 until width) {
            var offset = startPos
            for (j in (height - 1) downTo 0) {  // 包含0
                nv12Rotate[k++] = nv12[offset + i]
                offset -= width
            }
        }

        // Rotate the U and V color components
        k = bufferSize - 1
            var x = width - 1
            while (x > 0) {
                var offset: Int = ySize
                for (y in 0 until height / 2) {
                    nv12Rotate[k] = nv12[offset + x]
                    k--
                    nv12Rotate[k] = nv12[offset + (x - 1)]
                    k--
                    offset += width
                }
                x -= 2
            }
    }
}