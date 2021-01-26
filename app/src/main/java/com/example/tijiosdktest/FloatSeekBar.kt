package com.example.tijiosdktest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class FloatSeekBar @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0)
    : View(context, attributeSet, defStyle) {

    var seekChange : ((progress: Int) -> Unit)? = null

    private var paint = Paint()

    private var bgRect = RectF()
    private var radius = 40

    private var drawRect = RectF()

    private var width = 0f
    private var height = 0f

    private var mCurrentProgress = 0

    private var mScale = 0f

    private val runnable = Runnable {
        val value = percent2Value(mScale)
        val percent = value2Percent(value)
        setProgress(percent)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        Log.i("zzzzzzzzzzzzzz", "$width ,,, $height")
        bgRect.set(0f, 0f, width.toFloat(), height.toFloat())
        drawRect.set(0f, 0f, width.toFloat(), height.toFloat())

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_MOVE) {
            val top = if (event.y <= 0) {
                0f
            } else {
                if (event.y >= height) height else event.y
            }
            mScale = (height - top) / height
            mCurrentProgress = (mScale * 100).toInt()
            setProgress(mCurrentProgress)
        } else if (event?.action == MotionEvent.ACTION_UP) {
            /*if (event.y <= 0) {
                drawRect.top = 0f
            } else {
                drawRect.top = event.y
            }
            mScale = (height - drawRect.top) / height
            mCurrentProgress = (mScale * 100).toInt()
            setProgress(mCurrentProgress)*/
            Log.i("zzzzzzzzzzzzz", "$mScale ,, $mCurrentProgress")
            postDelayed(runnable, 1000)
        }
        return true
    }

    fun setProgress(progress: Int) {
        mCurrentProgress = progress
        val scale = progress.toFloat() / 100
        drawRect.bottom = height - (scale * height)
        Log.i("zzzzzzzzzzzzzzzz", "$mCurrentProgress")
        seekChange?.invoke(mCurrentProgress)
        invalidate()
    }

    fun getDrawTop() : Float {
        val scale = mCurrentProgress.toFloat() / 100
        return height - (scale * height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#88000000")
        canvas?.drawRoundRect(bgRect, radius.toFloat(), radius.toFloat(), paint)

        canvas?.clipRect(bgRect)  // 裁剪矩形 绘制进度
        canvas?.clipRect(drawRect, Region.Op.DIFFERENCE)  // 裁剪矩形 绘制进度

        paint.color = Color.WHITE
        canvas?.drawRoundRect(bgRect, radius.toFloat(), radius.toFloat(), paint)
    }

    fun percent2Value(percent: Float) : Int {
        return (percent * 255).toInt()
    }

    fun value2Percent(value: Int) : Int {
        return (value.toFloat() / 255 * 100).toInt()
    }
}