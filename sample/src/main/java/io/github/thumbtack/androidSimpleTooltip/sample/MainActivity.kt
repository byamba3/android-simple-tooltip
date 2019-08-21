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

package io.github.thumbtack.androidSimpleTooltip.sample

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import io.github.thumbtack.androidSimpleTooltip.OverlayView
import io.github.thumbtack.androidSimpleTooltip.SimpleTooltip
import io.github.thumbtack.androidSimpleTooltip.SimpleTooltipUtils

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(this)

        findViewById<Button>(R.id.btn_simple).setOnClickListener(this)
        findViewById<Button>(R.id.btn_animated).setOnClickListener(this)
        findViewById<Button>(R.id.btn_overlay).setOnClickListener(this)
        findViewById<Button>(R.id.btn_maxwidth).setOnClickListener(this)
        findViewById<Button>(R.id.btn_outside).setOnClickListener(this)
        findViewById<Button>(R.id.btn_inside).setOnClickListener(this)
        findViewById<Button>(R.id.btn_inside_modal).setOnClickListener(this)
        findViewById<Button>(R.id.btn_modal_custom).setOnClickListener(this)
        findViewById<Button>(R.id.btn_no_arrow).setOnClickListener(this)
        findViewById<Button>(R.id.btn_custom_arrow).setOnClickListener(this)
        findViewById<Button>(R.id.btn_dialog).setOnClickListener(this)
        findViewById<Button>(R.id.btn_center).setOnClickListener(this)
        findViewById<Button>(R.id.btn_overlay_rect).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = "Floating action Button",
                        onDismissListener = object : SimpleTooltip.OnDismissListener {
                            override fun onDismiss(tooltip: SimpleTooltip) {
                                println("dismiss $tooltip")
                            }
                        },
                        onShowListener = object : SimpleTooltip.OnShowListener {
                            override fun onShow(tooltip: SimpleTooltip) {
                                println("show $tooltip")
                            }
                        },
                        gravity = Gravity.START
                ).show()
            }
            R.id.btn_simple -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_simple),
                        gravity = Gravity.END
                ).show()
            }
            R.id.btn_animated -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_animated),
                        gravity = Gravity.TOP,
                        animated = true
                ).show()
            }
            R.id.btn_overlay -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_overlay),
                        gravity = Gravity.START,
                        animated = true,
                        transparentOverlay = false,
                        overlayWindowBackgroundColor = Color.BLACK
                ).show()
            }
            R.id.btn_maxwidth -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth),
                        gravity = Gravity.END,
                        maxWidth = R.dimen.simpletooltip_max_width.toFloat()
                ).show()
            }
            R.id.btn_outside -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_outside),
                        gravity = Gravity.BOTTOM,
                        dismissOnOutsideTouch = true,
                        dismissOnInsideTouch = false
                ).show()
            }
            R.id.btn_inside -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_inside),
                        gravity = Gravity.START,
                        dismissOnOutsideTouch = false,
                        dismissOnInsideTouch = true
                ).show()
            }
            R.id.btn_inside_modal -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_inside_modal),
                        gravity = Gravity.END,
                        dismissOnOutsideTouch = false,
                        dismissOnInsideTouch = true,
                        modal = true
                ).show()
            }
            R.id.btn_modal_custom -> {
                val tooltip = SimpleTooltip(
                        context = this@MainActivity,
                        anchorView = v,
                        text = getString(R.string.btn_modal_custom),
                        gravity = Gravity.TOP,
                        dismissOnOutsideTouch = false,
                        dismissOnInsideTouch = false,
                        modal = true,
                        animated = true,
                        animationDuration = 2000,
                        animationPadding = SimpleTooltipUtils.pxFromDp(50f),
                        customViewLayout = layoutInflater.inflate(R.layout.tooltip_custom, null, false),
                        focusable = true
                )

                val editText = tooltip.findViewById(R.id.ed_text) as EditText

                tooltip.findViewById<Button>(R.id.btn_next).setOnClickListener {
                    if (tooltip.isShowing)
                        tooltip.dismiss()
                    SimpleTooltip(
                            context = v.context,
                            anchorView = v,
                            text = editText.text,
                            gravity = Gravity.BOTTOM
                    ).show()
                }

                tooltip.show()
            }
            R.id.btn_no_arrow -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_no_arrow),
                        gravity = Gravity.START,
                        showArrow = false,
                        modal = true,
                        animated = true
                ).show()
            }
            R.id.btn_custom_arrow -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_custom_arrow),
                        gravity = Gravity.END,
                        modal = true,
                        arrowDrawable = SimpleTooltipUtils.getDrawable(this, android.R.drawable.ic_media_previous),
                        arrowHeight = SimpleTooltipUtils.pxFromDp(50f),
                        arrowWidth = SimpleTooltipUtils.pxFromDp(50f)
                ).show()
            }
            R.id.btn_dialog -> {
                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(R.layout.dialog)
                dialog.show()

                val btnInDialog = dialog.findViewById(R.id.btn_in_dialog) as Button
                btnInDialog.setOnClickListener {
                    SimpleTooltip(
                            context = this@MainActivity,
                            anchorView = btnInDialog,
                            text = getString(R.string.btn_in_dialog),
                            gravity = Gravity.BOTTOM,
                            animated = true,
                            transparentOverlay = false,
                            overlayMatchParent = false
                    ).show()
                }

                val btnClose = dialog.findViewById(R.id.btn_close) as Button
                btnClose.setOnClickListener { dialog.dismiss() }
            }
            R.id.btn_center -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v.rootView,
                        text = getString(R.string.btn_center),
                        showArrow = false,
                        gravity = Gravity.CENTER
                ).show()
            }
            R.id.btn_overlay_rect -> {
                SimpleTooltip(
                        context = this,
                        anchorView = v,
                        text = getString(R.string.btn_overlay_rect),
                        gravity = Gravity.END,
                        animated = true,
                        transparentOverlay = false,
                        highlightShape = OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR,
                        overlayOffset = 0f
                ).show()
            }
        }
    }
}
