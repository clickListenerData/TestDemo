package com.example.tijiosdktest.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.*
import java.util.*

class CustomParentView (context: Context,attributeSet: AttributeSet) : FrameLayout(context,attributeSet) , NestedScrollingParent3{

    private val parentHelper by lazy { NestedScrollingParentHelper(this) }
    private val childHelper by lazy { NestedScrollingChildHelper(this) }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        Log.i("CustomParentView","on start :: $child ,,, $target ,, $axes  ,, $type")
        return true
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        Log.i("CustomParentView","on nested accepted :: $child ,,, $target ,, $axes  ,, $type")
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        Log.i("CustomParentView","on stop : ,,, $target  ,, $type")
        parentHelper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        Log.i("CustomParentView","on nested scroll 1 :: $dxConsumed ,,, $target ,, $dyConsumed  ,, $type ,, $dxUnconsumed ,, $dyUnconsumed ,, $consumed")
        childHelper.dispatchNestedScroll(dxConsumed,dyConsumed,dxUnconsumed,dyUnconsumed,null,type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        Log.i("CustomParentView","on nested scroll 2 :: $dxConsumed ,,, $target ,, $dyConsumed  ,, $type ,, $dxUnconsumed ,, $dyUnconsumed")
        childHelper.dispatchNestedScroll(dxConsumed,dyConsumed,dxUnconsumed,dyUnconsumed,null,type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.i("CustomParentView","on pre scroll :: $dx ,,, $target ,, $dy  ,, $type ,, ${consumed.contentToString()}")
        childHelper.dispatchNestedPreScroll(dx,100,consumed,null)
    }
}