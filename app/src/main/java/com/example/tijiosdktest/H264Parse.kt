package com.example.tijiosdktest

        fun main1(args: Array<String>) {  // H264 哥伦布编码解析  以0的位数计算数据的长度

            val data = (4 + 1) and 0xff  // 0000 0101  模拟数据4 +1 为了数据0凑墙
            var nStartBits = 3   //从第3位开始计算
            //计算0的个数
            var nZeroNum = 0
            while (true) {
                if (data and (0x80 shr nStartBits) != 0) {  // 找到第一个1的位置
                    break
                }
                nZeroNum++
                nStartBits++
            }

            // 计算数据的10进制值
            nStartBits++
            var dwResult = 0
            for (i in 0 until nZeroNum) {  // 计算后面的值
                dwResult = dwResult shl 2
                if (data and (0x80 shr nStartBits) != 0) {
                    dwResult += 1
                }
                nStartBits++
            }
            dwResult += (1 shl nZeroNum)  // 加上 墙 的值
            println(dwResult - 1)  // 最终结果 -1

//            val num = 5
//            println("${Integer.toHexString(num)} ,,, ${Integer.toBinaryString(num)} ,,, ${Integer.toOctalString(num)} ,,, ${num.toString(16)} ,,, ${Integer.highestOneBit(num)} ,,, ${Integer.lowestOneBit(num)}")
        }

fun main() {
}