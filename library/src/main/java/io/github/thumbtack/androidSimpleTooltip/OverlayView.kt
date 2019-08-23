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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View

/**
 * View that makes the screen darken and highlight the anchor point. <br></br>
 * Implementation based on: http://stackoverflow.com/a/34702884/2826279
 *
 * Created by douglas on 09/05/16.
 */
@SuppressLint("ViewConstructor")
class OverlayView internal constructor(
    context: Context,
    private var mAnchorView: View?,
    private val highlightShape: Int,
    private val mOffset: Float,
    private val overlayWindowBackground: Int
) : View(context) {

    private var bitmap: Bitmap? = null
    private var invalidated = true

    var anchorView: View?
        get() = mAnchorView
        set(anchorView) {
            this.mAnchorView = anchorView
            invalidate()
        }

    override fun dispatchDraw(canvas: Canvas) {
        if (invalidated || bitmap == null || bitmap?.isRecycled == true)
            createWindowFrame()
        // The bitmap is checked again because of Android memory cleanup behavior. (See #42)
        if (bitmap != null && bitmap?.isRecycled == false)
            canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    private fun createWindowFrame() {
        val width = measuredWidth
        val height = measuredHeight
        if (width <= 0 || height <= 0)
            return

        bitmap?.let {
            if (!it.isRecycled)
                it.recycle()
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val osCanvas = Canvas(bitmap!!)

        val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = overlayWindowBackground
        paint.isAntiAlias = true
        paint.alpha = resources.getInteger(mDefaultOverlayAlphaRes)
        osCanvas.drawRect(outerRectangle, paint)

        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)

        mAnchorView?.let {
            val anchorRecr = SimpleTooltipUtils.calculeRectInWindow(mAnchorView!!)
            val overlayRecr = SimpleTooltipUtils.calculeRectInWindow(this)

            val left = anchorRecr.left - overlayRecr.left
            val top = anchorRecr.top - overlayRecr.top

            val rect = RectF(
                left - mOffset,
                top - mOffset,
                left + mAnchorView!!.measuredWidth.toFloat() + mOffset,
                top + mAnchorView!!.measuredHeight.toFloat() + mOffset
            )

            if (highlightShape == HIGHLIGHT_SHAPE_RECTANGULAR) {
                osCanvas.drawRect(rect, paint)
            } else {
                osCanvas.drawOval(rect, paint)
            }
        }

        invalidated = false
    }

    override fun isInEditMode(): Boolean {
        return true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        invalidated = true
    }

    companion object {

        val HIGHLIGHT_SHAPE_OVAL = 0
        val HIGHLIGHT_SHAPE_RECTANGULAR = 1
        private val mDefaultOverlayAlphaRes = R.integer.simpletooltip_overlay_alpha
    }
}
