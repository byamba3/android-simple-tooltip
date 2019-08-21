/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Douglas Nassif Roma Junior
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.thumbtack.androidSimpleTooltip

import android.content.Context
import android.content.res.Resources
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StyleRes
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView

/**
 * SimpleTooltipUtils
 * Created by douglas on 09/05/16.
 */
object SimpleTooltipUtils {

    fun calculeRectOnScreen(view: View): RectF {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return RectF(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + view.measuredWidth).toFloat(),
            (location[1] + view.measuredHeight).toFloat()
        )
    }

    fun calculeRectInWindow(view: View): RectF {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return RectF(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + view.measuredWidth).toFloat(),
            (location[1] + view.measuredHeight).toFloat()
        )
    }

    fun dpFromPx(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    fun pxFromDp(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    fun setWidth(view: View, width: Float) {
        var params: ViewGroup.LayoutParams? = view.layoutParams
        if (params == null) {
            params = ViewGroup.LayoutParams(width.toInt(), view.height)
        } else {
            params.width = width.toInt()
        }
        view.layoutParams = params
    }

    fun tooltipGravityToArrowDirection(tooltipGravity: Int): Int {
        when (tooltipGravity) {
            Gravity.START -> return ArrowDrawable.RIGHT
            Gravity.END -> return ArrowDrawable.LEFT
            Gravity.TOP -> return ArrowDrawable.BOTTOM
            Gravity.BOTTOM -> return ArrowDrawable.TOP
            Gravity.CENTER -> return ArrowDrawable.TOP
            else -> throw IllegalArgumentException("Gravity must have be CENTER, START, END, TOP or BOTTOM.")
        }
    }

    fun setX(view: View, x: Int) {
        view.x = x.toFloat()
    }

    fun setY(view: View, y: Int) {
        view.y = y.toFloat()
    }

    private fun getOrCreateMarginLayoutParams(view: View): ViewGroup.MarginLayoutParams {
        val lp = view.layoutParams
        return if (lp != null) {
            lp as? ViewGroup.MarginLayoutParams ?: ViewGroup.MarginLayoutParams(lp)
        } else {
            ViewGroup.MarginLayoutParams(view.width, view.height)
        }
    }

    fun removeOnGlobalLayoutListener(
        view: View,
        listener: ViewTreeObserver.OnGlobalLayoutListener
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        } else {

            view.viewTreeObserver.removeGlobalOnLayoutListener(listener)
        }
    }

    fun setTextAppearance(tv: TextView, @StyleRes textAppearanceRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAppearance(textAppearanceRes)
        } else {

            tv.setTextAppearance(tv.context, textAppearanceRes)
        }
    }

    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(colorRes)
        } else {

            context.resources.getColor(colorRes)
        }
    }

    fun getDrawable(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getDrawable(drawableRes)
        } else {

            context.resources.getDrawable(drawableRes)
        }
    }

    /**
     * Verify if the first child of the rootView is a FrameLayout.
     * Used for cases where the Tooltip is created inside a Dialog or DialogFragment.
     *
     * @param anchorView
     * @return FrameLayout or anchorView.getRootView()
     */
    fun findFrameLayout(anchorView: View): ViewGroup {
        var rootView = anchorView.rootView as ViewGroup
        if (rootView.childCount == 1 && rootView.getChildAt(0) is FrameLayout) {
            rootView = rootView.getChildAt(0) as ViewGroup
        }
        return rootView
    }
}
