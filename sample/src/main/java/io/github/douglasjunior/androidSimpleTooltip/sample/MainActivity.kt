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

package io.github.douglasjunior.androidSimpleTooltip.sample

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip

/**
 * MainActivity
 * Created by douglas on 09/05/16.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(this)

        findViewById(R.id.btn_simple).setOnClickListener(this)
        findViewById(R.id.btn_animated).setOnClickListener(this)
        findViewById(R.id.btn_overlay).setOnClickListener(this)
        findViewById(R.id.btn_maxwidth).setOnClickListener(this)
        findViewById(R.id.btn_outside).setOnClickListener(this)
        findViewById(R.id.btn_inside).setOnClickListener(this)
        findViewById(R.id.btn_inside_modal).setOnClickListener(this)
        findViewById(R.id.btn_modal_custom).setOnClickListener(this)
        findViewById(R.id.btn_no_arrow).setOnClickListener(this)
        findViewById(R.id.btn_custom_arrow).setOnClickListener(this)
        findViewById(R.id.btn_dialog).setOnClickListener(this)
        findViewById(R.id.btn_center).setOnClickListener(this)
        findViewById(R.id.btn_overlay_rect).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.fab) {
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

        } else if (v.id == R.id.btn_simple) {
            SimpleTooltip(
                    context = this,
                    anchorView = v,
                    text = getString(R.string.btn_simple),
                    gravity = Gravity.END
            ).show()

        } else if (v.id == R.id.btn_animated) {

            SimpleTooltip(
                    context = this,
                    anchorView = v,
                    text = getString(R.string.btn_animated),
                    gravity = Gravity.TOP,
                    animated = true
            ).show()

        } else if (v.id == R.id.btn_overlay) {
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

        /**
        else if (v.id == R.id.btn_maxwidth) {
        SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth) + getString(R.string.btn_maxwidth))
        .gravity(Gravity.END)
        .maxWidth(R.dimen.simpletooltip_max_width)
        .build()
        .show()

        } else if (v.id == R.id.btn_outside) {
        SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(R.string.btn_outside)
        .gravity(Gravity.BOTTOM)
        .dismissOnOutsideTouch(true)
        .dismissOnInsideTouch(false)
        .build()
        .show()

        } else if (v.id == R.id.btn_inside) {
        SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(R.string.btn_inside)
        .gravity(Gravity.START)
        .dismissOnOutsideTouch(false)
        .dismissOnInsideTouch(true)
        .build()
        .show()

        } else if (v.id == R.id.btn_inside_modal) {
        SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(R.string.btn_inside_modal)
        .gravity(Gravity.END)
        .dismissOnOutsideTouch(false)
        .dismissOnInsideTouch(true)
        .modal(true)
        .build()
        .show()

        } else if (v.id == R.id.btn_modal_custom) {
        val tooltip = SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(R.string.btn_modal_custom)
        .gravity(Gravity.TOP)
        .dismissOnOutsideTouch(false)
        .dismissOnInsideTouch(false)
        .modal(true)
        .animated(true)
        .animationDuration(2000)
        .animationPadding(SimpleTooltipUtils.pxFromDp(50f))
        .contentView(R.layout.tooltip_custom, R.id.tv_text)
        .focusable(true)
        .build()

        val ed = tooltip.findViewById(R.id.ed_text)

        tooltip.findViewById(R.id.btn_next).setOnClickListener(View.OnClickListener {
        if (tooltip.isShowing())
        tooltip.dismiss()
        SimpleTooltip.Builder(v.context)
        .anchorView(v)
        .text(ed.getText())
        .gravity(Gravity.BOTTOM)
        .build()
        .show()
        })

        tooltip.show()
        } else if (v.id == R.id.btn_no_arrow) {
        SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(R.string.btn_no_arrow)
        .gravity(Gravity.START)
        .showArrow(false)
        .modal(true)
        .animated(true)
        .build()
        .show()

        } else if (v.id == R.id.btn_custom_arrow) {
        SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(R.string.btn_custom_arrow)
        .gravity(Gravity.END)
        .modal(true)
        .arrowDrawable(android.R.drawable.ic_media_previous)
        .arrowHeight(SimpleTooltipUtils.pxFromDp(50f).toInt())
        .arrowWidth(SimpleTooltipUtils.pxFromDp(50f).toInt())
        .build()
        .show()

        } else if (v.id == R.id.btn_dialog) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog)
        dialog.show()

        val btnInDialog = dialog.findViewById(R.id.btn_in_dialog) as Button
        btnInDialog.setOnClickListener {
        SimpleTooltip.Builder(this@MainActivity)
        .anchorView(btnInDialog)
        .text(R.string.btn_in_dialog)
        .gravity(Gravity.BOTTOM)
        .animated(true)
        .transparentOverlay(false)
        .overlayMatchParent(false)
        .build()
        .show()
        }
        val btnClose = dialog.findViewById(R.id.btn_close) as Button
        btnClose.setOnClickListener { dialog.dismiss() }
        } else if (v.id == R.id.btn_center) {
        SimpleTooltip.Builder(this)
        .anchorView(v.rootView)
        .text(R.string.btn_center)
        .showArrow(false)
        .gravity(Gravity.CENTER)
        .build()
        .show()
        } else if (v.id == R.id.btn_overlay_rect) {
        SimpleTooltip.Builder(this)
        .anchorView(v)
        .text(R.string.btn_overlay_rect)
        .gravity(Gravity.END)
        .animated(true)
        .transparentOverlay(false)
        .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
        .overlayOffset(0)
        .build()
        .show()
        }
         **/
    }
}
