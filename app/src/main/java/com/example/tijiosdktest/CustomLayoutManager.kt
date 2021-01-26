package com.example.tijiosdktest

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.abs

/**
 *  吸顶的 view 加入缓存
 *  移动时  先移除 吸顶view
 *  重新布局 吸顶view
 *
 */
class CustomLayoutManager(val orientation: Int) : RecyclerView.LayoutManager() {

    private var mCurrentPosition = 0

    private val sectionViews = Stack<View>()
    private val sectionMap = hashMapOf<Int, View>()

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return super.isAutoMeasureEnabled()
    }


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
//        LinearLayoutManager
//        RecyclerView
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun canScrollHorizontally(): Boolean {
        return orientation == LinearLayoutManager.HORIZONTAL
    }

    override fun canScrollVertically(): Boolean {
        return orientation == LinearLayoutManager.VERTICAL
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)

        if (itemCount <= 0) return
        if (state?.isPreLayout == true) return
        // 将所有item分离至scrap中  布局区域内的view加入缓存
        detachAndScrapAttachedViews(recycler!!)
        layoutChunk(recycler)
    }

    private fun layoutChunk(recycler: RecyclerView.Recycler) {
        if (canScrollHorizontally()) {
            var itemLeft = paddingLeft
            var i = 0  // 从第0个item开始
            while (true) {
                if (itemLeft >= width - paddingRight) {
                    break
                }
                if (i >= itemCount) {
                    break
                }
                val view = recycler.getViewForPosition(i)  // 从缓存中获取子view
                addView(view)  // 添加子view
                measureChildWithMargins(view, 0, 0)// 测量子view

                val right = itemLeft + getDecoratedMeasuredWidth(view)
                val top = paddingTop
                val bottom = top + getDecoratedMeasuredHeight(view)

                layoutDecorated(view, itemLeft, top, right, bottom)  // 布局子view

                itemLeft = right
                i++

            }
        } else {
            var itemTop = paddingTop
            var i = 0
            while (itemTop < height - paddingBottom && i < itemCount) {
                val view = recycler.getViewForPosition(i)
                addView(view)
                measureChildWithMargins(view, 0, 0)

                val left = paddingLeft
                val right = left + getDecoratedMeasuredWidth(view)
                val bottom = itemTop + getDecoratedMeasuredHeight(view)

                layoutDecorated(view, left, itemTop, right, bottom)

                itemTop = bottom
                i++
            }
        }
    }

    /**
     * 子view滑动  填充子view
     * dx：移动距离  >0 左滑
     * 返回实际滚动距离
     */
    override fun scrollHorizontallyBy(
            dx: Int,
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
    ): Int {
        // 填充 下一个可见view
        fill(dx > 0, recycler!!)
        offsetChildrenHorizontal(-dx) // 对所有可见子view 偏移
        // 回收 不可见子view
        recyclerChildView(dx > 0, recycler)
        return dx
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        dealSectionView(dy)
        sectionViews.forEach { removeView(it) }
        val consumed = fillVertical(dy, recycler!!)
        offsetChildrenVertical(-consumed)
        recyclerVerticalView(dy, recycler)
        addSectionView()
        return consumed
    }

    private fun dealSectionView(dy: Int) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: return
            val top = getDecoratedTop(view)
            if (getItemViewType(view) == 99 && dy > 0) {
                if (top - dy <= 0 && !sectionViews.contains(view)) {
                    sectionViews.push(view)
                    sectionMap[getPosition(view)] = view
                }
            }
        }
    }

    private fun addSectionView() {
        val firstView = getChildAt(0) ?: return
        if (getItemViewType(firstView) == 99 && sectionViews.peek() != null) {
            removeViewAt(0)
        }
        val position = getPosition(firstView)
        val removes = arrayListOf<Int>()
        sectionMap.forEach {
            if (it.key > position) {
                removes.add(it.key)
            }
        }
        removes.forEach {
            sectionViews.remove(sectionMap[it])
            sectionMap.remove(it)
        }
        val peek = sectionViews.peek()
        val subItem = getChildAt(1) ?: return
        if (getItemViewType(subItem) == 99) {
            val h: Int = peek.getMeasuredHeight()
            val top = Math.min(0, -(h - subItem.top))
            val bottom = Math.min(h, subItem.top)
            peek.layout(0, top, peek.getMeasuredWidth(), bottom)
        } else {
            peek.layout(0, 0, peek.measuredWidth, peek.measuredHeight)
        }
    }

    private fun fill(fillEnd: Boolean, recycler: RecyclerView.Recycler) {
        if (childCount == 0) return
        if (fillEnd) {
            // 填充 尾部
            val lastView = getChildAt(childCount - 1) ?: return
            val itemPosition = getPosition(lastView)
            val lastRight = width - paddingRight
            var scrapRight = lastView.right  // 最后一个可见view 右边距
            while (scrapRight < lastRight) {
                if (itemPosition == itemCount - 1) break
                val currentPosition = itemPosition + 1
                val currentView = recycler.getViewForPosition(currentPosition)
                addView(currentView)
                measureChildWithMargins(currentView, 0, 0)

                val top = paddingTop
                val bottom = top + getDecoratedMeasuredHeight(currentView)
                val right = scrapRight + getDecoratedMeasuredWidth(currentView)
                layoutDecorated(currentView, scrapRight, top, right, bottom)

                scrapRight = right
            }
        } else {
            val firstView = getChildAt(0) ?: return
            val itemPosition = getPosition(firstView)

            var scrapLeft = firstView.left
            while (scrapLeft > paddingLeft) {
                val currentPosition = itemPosition - 1
                if (currentPosition < 0) break
                val currentView = recycler.getViewForPosition(currentPosition)
                addView(currentView, 0)
                measureChildWithMargins(currentView, 0, 0)

                val left = scrapLeft - getDecoratedMeasuredWidth(currentView)
                val top = paddingTop
                val bottom = top + getDecoratedMeasuredHeight(currentView)
                layoutDecorated(currentView, left, top, scrapLeft, bottom)

                scrapLeft = left
            }
        }

    }

    private fun fillVertical(dx: Int, recycler: RecyclerView.Recycler) : Int {
        //将要填充的position
        var fillPosition = RecyclerView.NO_POSITION
        //可用的空间，和onLayoutChildren中的totalSpace类似
        var availableSpace = abs(dx)
        //增加一个滑动距离的绝对值，方便计算
        val absDelta = abs(dx)

        //将要填充的View的左上右下
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0

        //dx>0就是手指从右滑向左，所以就要填充尾部
        if (dx > 0) {
            val anchorView = getChildAt(childCount - 1)!!
            val anchorPosition = getPosition(anchorView)
            val anchorRight = getDecoratedBottom(anchorView)

            top = anchorRight
            //填充尾部，那么下一个position就应该是+1
            fillPosition = anchorPosition + 1

            //如果要填充的position超过合理范围并且最后一个View的
            //right-移动的距离 < 右边缘(width)那就要修正真实能移动的距离
            if (fillPosition >= itemCount && anchorRight - absDelta < height) {
                val fixScrolled = anchorRight - height
                return fixScrolled
            }

            //如果尾部的锚点位置减去dx还是在屏幕外，就不填充下一个View
            if (anchorRight - absDelta > height) {
                return dx
            }
        }

        //dx<0就是手指从左滑向右，所以就要填充头部
        if (dx < 0) {
            val anchorView = getChildAt(0)!!
            val anchorPosition = getPosition(anchorView)
            val anchorLeft = getDecoratedTop(anchorView)

            bottom = anchorLeft
            //填充头部，那么上一个position就应该是-1
            fillPosition = anchorPosition - 1

            //如果要填充的position超过合理范围并且第一个View的
            //left+移动的距离 > 左边缘(0)那就要修正真实能移动的距离
            if (fillPosition < 0 && anchorLeft + absDelta > 0) {
                return anchorLeft
            }

            //如果头部的锚点位置加上dx还是在屏幕外，就不填充上一个View
            if (anchorLeft + absDelta < 0) {
                return dx
            }
        }

        //根据限定条件，不停地填充View进来
        while (availableSpace > 0 && (fillPosition in 0 until itemCount)) {
            Log.i("zzzzzzzzzzzzzzzzzzz", "${fillPosition}")
            val itemView = recycler.getViewForPosition(fillPosition)

            if (dx > 0) {
                addView(itemView)
            } else {
//                if (getItemViewType(itemView) == 99 && fillPosition != 0) sectionViews.pop()
                addView(itemView, 0)
            }

            measureChildWithMargins(itemView, 0, 0)

            if (dx > 0) {
                bottom = top + getDecoratedMeasuredHeight(itemView)
            } else {
                top = bottom - getDecoratedMeasuredHeight(itemView)
            }

            right = left + getDecoratedMeasuredWidth(itemView)
            layoutDecorated(itemView, left, top, right, bottom)

            if (dx > 0) {
                top += getDecoratedMeasuredHeight(itemView)
                fillPosition++
            } else {
                bottom -= getDecoratedMeasuredHeight(itemView)
                fillPosition--
            }

            if (fillPosition in 0 until itemCount) {
                availableSpace -= getDecoratedMeasuredHeight(itemView)
            }
        }

        return dx
    }

    /**
     * @param fillEnd 方向
     */
    private fun recyclerChildView(fillEnd: Boolean, recycler: RecyclerView.Recycler) {
        var i = 0
        if (fillEnd) {
            // 回收 头部 子view
            while (true) {
                val childView = getChildAt(i) ?: return
                if (childView.right < paddingLeft) {
                    removeAndRecycleView(childView, recycler)
                }
                i++
            }
        } else {
            i = childCount - 1
            while (true) {
                val childView = getChildAt(i) ?: return
                if (childView.left > width - paddingRight) {
                    removeAndRecycleView(childView, recycler)
                }
                i--
            }
        }
    }

    private fun recyclerVerticalView(dy: Int, recycler: RecyclerView.Recycler) {
        var i = 0
        if (dy > 0) {
            // 回收 头部 子view
            while (true) {
                val childView = getChildAt(i) ?: return
                if (childView.bottom < paddingTop) {
                    removeAndRecycleView(childView, recycler)
                }
                i++
            }
        } else {
            i = childCount - 1
            while (true) {
                val childView = getChildAt(i) ?: return
                if (childView.top > height - paddingBottom) {
                    removeAndRecycleView(childView, recycler)
                }
                i--
            }
        }
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
    }

    override fun smoothScrollToPosition(
            recyclerView: RecyclerView?,
            state: RecyclerView.State?,
            position: Int
    ) {
        super.smoothScrollToPosition(recyclerView, state, position)
    }






}