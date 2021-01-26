package com.example.tijiosdktest.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class VoiceAnimView(context: Context,attributeSet: AttributeSet) : View(context,attributeSet) {

    private val paint = Paint()

//    private val path = Path()

    private val topPoint = Point()
    private val bottomPoint = Point()

    private val cenPoint = Point()

    private var type = 0

    private val runnable = Runnable {
//        topPoint.x = width / 2
//        bottomPoint.x = width / 2
        if (type == 0) {
            topPoint.y += 10
            if (topPoint.y > height / 2) {
                type = 1
            }
            /*if (topPoint.y > height - 10) {
                type = 2
            }*/
        }
        if (type == 1) {
            bottomPoint.y -= 10
            if (bottomPoint.y < height / 2) {
                type = 2
            }
        }
        if (type == 2) {
            topPoint.y -= 10
            if (topPoint.y < 10) {
                type = 3
            }
        }
        if (type == 3) {
            bottomPoint.y += 10
            if (bottomPoint.y > height - 10) {
                type = 0
            }
        }
        invalidate()
    }

    init {
        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topPoint.set(width / 2,0)
        bottomPoint.set(width / 2,height)

        cenPoint.set(width / 4,height / 4)
    }

    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)

        canvas?.drawCircle(width / 2f,height / 2f,width / 2f - 5f,paint)

        /*canvas?.save()
        val path = Path()
        path.moveTo(0f,height / 2f)
        path.quadTo(cenPoint.x.toFloat(),cenPoint.y.toFloat(),topPoint.x.toFloat(),topPoint.y.toFloat())
//        path.lineTo(topPoint.x.toFloat(),topPoint.y.toFloat())

        canvas?.drawPath(path,paint)

        val path2 = Path()
        path2.moveTo(0f,height / 2f)
        path2.quadTo(width / 4f,height * 3 / 4f,bottomPoint.x.toFloat(),bottomPoint.y.toFloat())
//        path.lineTo(bottomPoint.x.toFloat(),bottomPoint.y.toFloat())

        canvas?.drawPath(path2,paint)

        val path3 = Path()
        path3.moveTo(width.toFloat(),height / 2f)
        path3.quadTo(width * 3 / 4f,height / 4f,topPoint.x.toFloat(),topPoint.y.toFloat())
//        path.lineTo(bottomPoint.x.toFloat(),bottomPoint.y.toFloat())

        canvas?.drawPath(path3,paint)

        val path4 = Path()
        path4.moveTo(width.toFloat(),height / 2f)
        path4.quadTo(width * 3 / 4f,height * 3 / 4f,bottomPoint.x.toFloat(),bottomPoint.y.toFloat())
//        path.lineTo(bottomPoint.x.toFloat(),bottomPoint.y.toFloat())

        canvas?.drawPath(path4,paint)

        postDelayed(runnable,16)*/
    }
}