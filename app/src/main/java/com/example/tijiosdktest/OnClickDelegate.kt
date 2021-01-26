package com.example.tijiosdktest

import android.view.View
import androidx.annotation.CallSuper
import java.lang.reflect.Modifier

class OnClickDelegate(val listener: View.OnClickListener) : View.OnClickListener {

    var clickTime : Long = 0L

    override fun onClick(v: View?) {
        if (clickTime != 0L) {
            val time = System.currentTimeMillis() - clickTime
            if (time > 500) {
                dealClick(v)
            }
        } else {
            dealClick(v)
        }
    }

    private fun dealClick(v: View?) {
        clickTime = System.currentTimeMillis()
        listener.onClick(v)
    }
}

inline fun View.setDoubleClickListener(listener: View.OnClickListener) {
    setOnClickListener(OnClickDelegate(listener))
    // 类 属性 方法
    // 源码 编译 运行
    /*setOnClickListener {

    }*/
}

// 注解处理器
// AOP  View.setOnClickListener javac
// gradle 修改字节码 ASM java ssi
