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

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt

/**
 * ArrowDrawable
 * Created by douglas on 09/05/16.
 */
class ArrowDrawable internal constructor(
    @ColorInt foregroundColor: Int,
    private val direction: Int
) : ColorDrawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundColor: Int = Color.TRANSPARENT
    private var path: Path? = null

    init {
        this.paint.color = foregroundColor
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updatePath(bounds)
    }

    @Synchronized
    private fun updatePath(bounds: Rect) {
        path = Path().apply {
            when (direction) {
                LEFT -> {
                    moveTo(bounds.width().toFloat(), bounds.height().toFloat())
                    lineTo(0f, (bounds.height() / 2).toFloat())
                    lineTo(bounds.width().toFloat(), 0f)
                    lineTo(bounds.width().toFloat(), bounds.height().toFloat())
                }
                TOP -> {
                    moveTo(0f, bounds.height().toFloat())
                    lineTo((bounds.width() / 2).toFloat(), 0f)
                    lineTo(bounds.width().toFloat(), bounds.height().toFloat())
                    lineTo(0f, bounds.height().toFloat())
                }
                RIGHT -> {
                    moveTo(0f, 0f)
                    lineTo(bounds.width().toFloat(), (bounds.height() / 2).toFloat())
                    lineTo(0f, bounds.height().toFloat())
                    lineTo(0f, 0f)
                }
                BOTTOM -> {
                    moveTo(0f, 0f)
                    lineTo((bounds.width() / 2).toFloat(), bounds.height().toFloat())
                    lineTo(bounds.width().toFloat(), 0f)
                    lineTo(0f, 0f)
                }
            }

            close()
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)
        if (path == null)
            updatePath(bounds)
        path?.let { canvas.drawPath(it, paint) }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColor(@ColorInt color: Int) {
        paint.color = color
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        if (paint.colorFilter != null) {
            return PixelFormat.TRANSLUCENT
        }

        when (paint.color.ushr(24)) {
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
