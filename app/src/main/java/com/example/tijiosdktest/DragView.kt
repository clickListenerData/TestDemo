package com.example.tijiosdktest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DragView @JvmOverloads constructor(context: Context,attributes: AttributeSet? = null,defStyle: Int = 0)
    : View(context,attributes,defStyle){

    private val paint = Paint()
    private val path = Path()

    private var dragWidth = 0f

    private var currentY = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        currentY = (height / 2).toFloat()
        dragWidth = 0f
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_MOVE) {
            currentY = event.y
            dragWidth = event.x + 120f
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.lineTo(0f,0f)
        path.lineTo(dragWidth,0f)
//        path.cubicTo()
    }

}