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

package io.github.douglasjunior.androidSimpleTooltip

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt

/**
 * ArrowDrawable
 * Created by douglas on 09/05/16.
 */
class ArrowDrawable internal constructor(
        @ColorInt foregroundColor: Int,
        private val mDirection: Int) : ColorDrawable() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackgroundColor: Int
    private var mPath: Path? = null

    init {
        this.mBackgroundColor = Color.TRANSPARENT
        this.mPaint.color = foregroundColor
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updatePath(bounds)
    }

    @Synchronized
    private fun updatePath(bounds: Rect) {
        mPath = Path()

        when (mDirection) {
            LEFT -> {
                mPath!!.moveTo(bounds.width().toFloat(), bounds.height().toFloat())
                mPath!!.lineTo(0f, (bounds.height() / 2).toFloat())
                mPath!!.lineTo(bounds.width().toFloat(), 0f)
                mPath!!.lineTo(bounds.width().toFloat(), bounds.height().toFloat())
            }
            TOP -> {
                mPath!!.moveTo(0f, bounds.height().toFloat())
                mPath!!.lineTo((bounds.width() / 2).toFloat(), 0f)
                mPath!!.lineTo(bounds.width().toFloat(), bounds.height().toFloat())
                mPath!!.lineTo(0f, bounds.height().toFloat())
            }
            RIGHT -> {
                mPath!!.moveTo(0f, 0f)
                mPath!!.lineTo(bounds.width().toFloat(), (bounds.height() / 2).toFloat())
                mPath!!.lineTo(0f, bounds.height().toFloat())
                mPath!!.lineTo(0f, 0f)
            }
            BOTTOM -> {
                mPath!!.moveTo(0f, 0f)
                mPath!!.lineTo((bounds.width() / 2).toFloat(), bounds.height().toFloat())
                mPath!!.lineTo(bounds.width().toFloat(), 0f)
                mPath!!.lineTo(0f, 0f)
            }
        }

        mPath!!.close()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(mBackgroundColor)
        if (mPath == null)
            updatePath(bounds)
        canvas.drawPath(mPath!!, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColor(@ColorInt color: Int) {
        mPaint.color = color
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        if (mPaint.colorFilter != null) {
            return PixelFormat.TRANSLUCENT
        }

        when (mPaint.color.ushr(24)) {
            255 -> return PixelFormat.OPAQUE
            0 -> return PixelFormat.TRANSPARENT
        }
        return PixelFormat.TRANSLUCENT
    }

    companion object {

        val LEFT = 0
        val TOP = 1
        val RIGHT = 2
        val BOTTOM = 3
        val AUTO = 4
    }
}
