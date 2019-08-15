package io.github.douglasjunior.androidSimpleTooltip

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView

class SimpleTooltip(
        customContentView: View? = null,
        private val context: Context,
        private var dismissOnInsideTouch: Boolean = true,
        private var dismissOnOutsideTouch: Boolean = true,
        private var modal: Boolean = false,
        private var text: CharSequence = "",
        private var anchorView: View,
        private var arrowDirection: Int = ArrowDrawable.AUTO,
        private var gravity: Int = Gravity.BOTTOM,
        private var transparentOverlay: Boolean = true,
        private var overlayOffset: Float =
                context.getResources().getDimension(mDefaultOverlayOffsetRes),
        private var overlayMatchParent: Boolean = true,
        private var maxWidth: Float = 0f,
        private var showArrow: Boolean = true,
        private var arrowDrawable: Drawable? = null,
        private var animated: Boolean = false,
        private var margin: Float = context.resources.getDimension(mDefaultMarginRes),
        private var padding: Float = context.resources.getDimension(mDefaultPaddingRes),
        private var animationPadding: Float =
                context.resources.getDimension(mDefaultAnimationPaddingRes),
        private var onDismissListener: OnDismissListener? = null,
        private var onShowListener: OnShowListener? = null,
        private var animationDuration: Long =
                context.resources.getInteger(mDefaultAnimationDurationRes).toLong(),
        private var backgroundColor: Int =
                SimpleTooltipUtils.getColor(context, mDefaultBackgroundColorRes),
        private var textColor: Int = SimpleTooltipUtils.getColor(context, mDefaultTextColorRes),
        private var arrowColor: Int = SimpleTooltipUtils.getColor(context, mDefaultArrowColorRes),
        private var arrowHeight: Float = 0f,
        private var arrowWidth: Float = 0f,
        private var focusable: Boolean = false,
        private var highlightShape: Int = OverlayView.HIGHLIGHT_SHAPE_OVAL,
        private var width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        private var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        private var ignoreOverlay: Boolean = false,
        private var overlayWindowBackgroundColor: Int = Color.BLACK
) : PopupWindow.OnDismissListener {

    private var popupWindow: PopupWindow? = null
    private lateinit var contentLayout: View
    private var overlay: View? = null
    private lateinit var arrowView: ImageView
    private var animator: AnimatorSet? = null
    private var rootView: ViewGroup? = SimpleTooltipUtils.findFrameLayout(anchorView)
    private var dismissed = false
    private val contentView: View by lazy {
        customContentView ?: TextView(context).also {
            SimpleTooltipUtils.setTextAppearance(it, mDefaultTextAppearanceRes)
            it.setBackgroundColor(backgroundColor)
            it.setTextColor(textColor)
        }
    }
    val isShowing: Boolean
        get() = popupWindow?.isShowing == true

    companion object {
        private val TAG = SimpleTooltip::class.java.simpleName

        // Default Resources
        private val mDefaultPopupWindowStyleRes = android.R.attr.popupWindowStyle
        private val mDefaultTextAppearanceRes = R.style.simpletooltip_default
        private val mDefaultBackgroundColorRes = R.color.simpletooltip_background
        private val mDefaultTextColorRes = R.color.simpletooltip_text
        private val mDefaultArrowColorRes = R.color.simpletooltip_arrow
        private val mDefaultMarginRes = R.dimen.simpletooltip_margin
        private val mDefaultPaddingRes = R.dimen.simpletooltip_padding
        private val mDefaultAnimationPaddingRes = R.dimen.simpletooltip_animation_padding
        private val mDefaultAnimationDurationRes = R.integer.simpletooltip_animation_duration
        private val mDefaultArrowWidthRes = R.dimen.simpletooltip_arrow_width
        private val mDefaultArrowHeightRes = R.dimen.simpletooltip_arrow_height
        private val mDefaultOverlayOffsetRes = R.dimen.simpletooltip_overlay_offset
    }

    private val overlayTouchListener = View.OnTouchListener { v, event -> modal }

    private val mLocationLayoutListener = object
        : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val popup = popupWindow
            if (popup == null || dismissed) return

            if (maxWidth > 0 && contentView.width > maxWidth) {
                SimpleTooltipUtils.setWidth(contentView, maxWidth)
                popup.update(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                return
            }

            SimpleTooltipUtils.removeOnGlobalLayoutListener(popup.contentView, this)
            popup.contentView.viewTreeObserver.addOnGlobalLayoutListener(mArrowLayoutListener)
            val location = calculePopupLocation()
            popup.isClippingEnabled = true
            popup.update(location.x.toInt(), location.y.toInt(), popup.width, popup.height)
            popup.contentView.requestLayout()
            createOverlay()
        }
    }

    private val mArrowLayoutListener = object
        : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val popup = popupWindow
            if (popup == null || dismissed) return

            SimpleTooltipUtils.removeOnGlobalLayoutListener(popup.contentView, this)

            popup.contentView.viewTreeObserver.addOnGlobalLayoutListener(mAnimationLayoutListener)
            popup.contentView.viewTreeObserver.addOnGlobalLayoutListener(mShowLayoutListener)
            if (showArrow) {
                val achorRect = SimpleTooltipUtils.calculeRectOnScreen(anchorView)
                val contentViewRect = SimpleTooltipUtils.calculeRectOnScreen(contentLayout)
                var x: Float
                var y: Float
                if (arrowDirection == ArrowDrawable.TOP || arrowDirection == ArrowDrawable.BOTTOM) {
                    x = contentLayout.paddingLeft + SimpleTooltipUtils.pxFromDp(2f)
                    val centerX = contentViewRect.width() / 2f - arrowView.width / 2f
                    val newX = centerX - (contentViewRect.centerX() - achorRect.centerX())
                    if (newX > x) {
                        if (newX + arrowView.width.toFloat() + x > contentViewRect.width()) {
                            x = contentViewRect.width() - arrowView.width.toFloat() - x
                        } else {
                            x = newX
                        }
                    }
                    y = arrowView.top.toFloat()
                    y = y + if (arrowDirection == ArrowDrawable.BOTTOM) -1 else +1
                } else {
                    y = contentLayout.paddingTop + SimpleTooltipUtils.pxFromDp(2f)
                    val centerY = contentViewRect.height() / 2f - arrowView.height / 2f
                    val newY = centerY - (contentViewRect.centerY() - achorRect.centerY())
                    if (newY > y) {
                        if (newY + arrowView.height.toFloat() + y > contentViewRect.height()) {
                            y = contentViewRect.height() - arrowView.height.toFloat() - y
                        } else {
                            y = newY
                        }
                    }
                    x = arrowView.left.toFloat()
                    x = x + if (arrowDirection == ArrowDrawable.RIGHT) -1 else +1
                }
                SimpleTooltipUtils.setX(arrowView, x.toInt())
                SimpleTooltipUtils.setY(arrowView, y.toInt())
            }
            popup.contentView.requestLayout()
        }
    }

    private val mShowLayoutListener = object
        : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val popup = popupWindow
            if (popup == null || dismissed) return

            SimpleTooltipUtils.removeOnGlobalLayoutListener(popup.contentView, this)


            onShowListener?.onShow(this@SimpleTooltip)
            onShowListener = null

            contentLayout.visibility = View.VISIBLE
        }
    }

    private val mAnimationLayoutListener = object
        : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val popup = popupWindow
            if (popup == null || dismissed) return

            SimpleTooltipUtils.removeOnGlobalLayoutListener(popup.contentView, this)

            if (animated) startAnimation()

            popup.contentView.requestLayout()
        }
    }

    /**
     * <div class="pt">Listener utilizado para chamar o <tt>SimpleTooltip#dismiss()</tt> quando a <tt>View</tt> root é encerrada sem que a tooltip seja fechada.
     * Pode ocorrer quando a tooltip é utilizada dentro de Dialogs.</div>
     */
    private val mAutoDismissLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val popup = popupWindow
        if (popup == null || dismissed) return@OnGlobalLayoutListener

        if (rootView?.isShown == false) dismiss()
    }

    init {
        if (showArrow) {
            if (arrowDirection == ArrowDrawable.AUTO)
                arrowDirection = SimpleTooltipUtils.tooltipGravityToArrowDirection(gravity)
            if (arrowDrawable == null)
                arrowDrawable = ArrowDrawable(arrowColor, arrowDirection)
            if (arrowWidth == 0f)
                arrowWidth = context.resources.getDimension(mDefaultArrowWidthRes)
            if (arrowHeight == 0f)
                arrowHeight = context.resources.getDimension(mDefaultArrowHeightRes)
        }

        configPopupWindow()
        configContentView()
    }

    private fun configPopupWindow() {
        popupWindow = PopupWindow(context, null, mDefaultPopupWindowStyleRes)
        popupWindow?.run {
            setOnDismissListener(this@SimpleTooltip)
            width = width
            height = height
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
            isTouchable = true
            setTouchInterceptor(View.OnTouchListener { v, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()

                if (!dismissOnOutsideTouch && event.action == MotionEvent.ACTION_DOWN &&
                        (x < 0 || x >= contentLayout.measuredWidth || y < 0 ||
                                y >= contentLayout.measuredHeight)
                ) {
                    return@OnTouchListener true
                } else if (!dismissOnOutsideTouch && event.action == MotionEvent.ACTION_OUTSIDE) {
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_DOWN && dismissOnInsideTouch) {
                    dismiss()
                    return@OnTouchListener true
                }
                false
            })
            isClippingEnabled = false
            isFocusable = focusable
        }
    }

    fun show() {
        verifyDismissed()

        contentLayout.viewTreeObserver.addOnGlobalLayoutListener(mLocationLayoutListener)
        contentLayout.viewTreeObserver.addOnGlobalLayoutListener(mAutoDismissLayoutListener)

        rootView?.let {
            it.post {
                if (rootView?.isShown == true) {
                    popupWindow?.showAtLocation(rootView, Gravity.NO_GRAVITY, it.width, it.height)
                } else
                    Log.e(TAG, "Tooltip can't be shown. Root view is invalid or closed.")
            }
        }
    }

    private fun verifyDismissed() {
        if (dismissed) {
            throw IllegalArgumentException("Tooltip has been dismissed.")
        }
    }

    private fun createOverlay() {
        if (ignoreOverlay) {
            return
        }
        overlay = if (transparentOverlay) View(context) else OverlayView(
                context, anchorView, highlightShape, overlayOffset, overlayWindowBackgroundColor
        )
        overlay?.let { overlay ->
            if (overlayMatchParent)
                overlay.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            else {
                rootView?.let {
                    overlay.layoutParams = ViewGroup.LayoutParams(it.width, it.height)
                }
            }
            overlay.setOnTouchListener(overlayTouchListener)
            rootView?.addView(overlay)
        }
    }

    private fun calculePopupLocation(): PointF {
        val location = PointF()

        val anchorRect = SimpleTooltipUtils.calculeRectInWindow(anchorView)
        val anchorCenter = PointF(anchorRect.centerX(), anchorRect.centerY())

        popupWindow?.let { popupWindow ->
            when (gravity) {
                Gravity.START -> {
                    location.x = anchorRect.left - popupWindow.contentView.width.toFloat() - margin
                    location.y = anchorCenter.y - popupWindow.contentView.height / 2f
                }
                Gravity.END -> {
                    location.x = anchorRect.right + margin
                    location.y = anchorCenter.y - popupWindow.contentView.height / 2f
                }
                Gravity.TOP -> {
                    location.x = anchorCenter.x - popupWindow.contentView.width / 2f
                    location.y = anchorRect.top - popupWindow.contentView.height.toFloat() - margin
                }
                Gravity.BOTTOM -> {
                    location.x = anchorCenter.x - popupWindow.contentView.width / 2f
                    location.y = anchorRect.bottom + margin
                }
                Gravity.CENTER -> {
                    location.x = anchorCenter.x - popupWindow.contentView.width / 2f
                    location.y = anchorCenter.y - popupWindow.contentView.height / 2f
                }
                else -> throw IllegalArgumentException(
                        "Gravity must have be CENTER, START, END, TOP or BOTTOM."
                )
            }
        }

        return location
    }

    private fun configContentView() {
        if (contentView is TextView) {
            val tv = contentView as TextView
            tv.text = text
        }

        contentView.setPadding(padding.toInt(), padding.toInt(), padding.toInt(), padding.toInt())

        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = if (
                arrowDirection == ArrowDrawable.LEFT || arrowDirection == ArrowDrawable.RIGHT
        ) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        val layoutPadding = (if (animated) animationPadding else 0f).toInt()
        linearLayout.setPadding(
                layoutPadding, layoutPadding, layoutPadding, layoutPadding
        )

        if (showArrow) {
            arrowView = ImageView(context)
            arrowView.setImageDrawable(arrowDrawable)
            val arrowLayoutParams: LinearLayout.LayoutParams

            if (arrowDirection == ArrowDrawable.TOP || arrowDirection == ArrowDrawable.BOTTOM) {
                arrowLayoutParams = LinearLayout.LayoutParams(
                        arrowWidth.toInt(), arrowHeight.toInt(), 0f
                )
            } else {
                arrowLayoutParams = LinearLayout.LayoutParams(
                        arrowHeight.toInt(), arrowWidth.toInt(), 0f
                )
            }

            arrowLayoutParams.gravity = Gravity.CENTER
            arrowView.layoutParams = arrowLayoutParams

            if (arrowDirection == ArrowDrawable.BOTTOM || arrowDirection == ArrowDrawable.RIGHT) {
                linearLayout.addView(contentView)
                linearLayout.addView(arrowView)
            } else {
                linearLayout.addView(arrowView)
                linearLayout.addView(contentView)
            }
        } else {
            linearLayout.addView(contentView)
        }

        val contentViewParams = LinearLayout.LayoutParams(width, height, 0f)
        contentViewParams.gravity = Gravity.CENTER
        contentView.layoutParams = contentViewParams

        contentLayout = linearLayout
        contentLayout.visibility = View.INVISIBLE
        popupWindow?.contentView = contentLayout
    }

    fun dismiss() {
        if (dismissed)
            return

        dismissed = true
        popupWindow?.dismiss()
    }

    fun <T : View> findViewById(id: Int): T {
        @Suppress("UNCHECKED_CAST")
        return contentLayout.findViewById(id) as T
    }

    override fun onDismiss() {
        dismissed = true

        animator?.removeAllListeners()
        animator?.end()
        animator?.cancel()
        animator = null

        if (rootView != null && overlay != null) {
            rootView?.removeView(overlay)
        }
        rootView = null
        overlay = null

        onDismissListener?.onDismiss(this)
        onDismissListener = null

        popupWindow?.let {
            SimpleTooltipUtils.removeOnGlobalLayoutListener(it.contentView, mLocationLayoutListener)
            SimpleTooltipUtils.removeOnGlobalLayoutListener(it.contentView, mArrowLayoutListener)
            SimpleTooltipUtils.removeOnGlobalLayoutListener(it.contentView, mShowLayoutListener)
            SimpleTooltipUtils.removeOnGlobalLayoutListener(
                    it.contentView, mAnimationLayoutListener
            )
            SimpleTooltipUtils.removeOnGlobalLayoutListener(
                    it.contentView, mAutoDismissLayoutListener
            )
            popupWindow = null
        }
    }

    private fun startAnimation() {
        val property = if (gravity == Gravity.TOP || gravity == Gravity.BOTTOM) "translationY" else
            "translationX"

        val anim1 = ObjectAnimator.ofFloat(
                contentLayout, property, -animationPadding, animationPadding
        )
        anim1.duration = animationDuration
        anim1.interpolator = AccelerateDecelerateInterpolator()

        val anim2 = ObjectAnimator.ofFloat(
                contentLayout, property, animationPadding, -animationPadding
        )
        anim2.duration = animationDuration
        anim2.interpolator = AccelerateDecelerateInterpolator()

        animator = AnimatorSet()
        animator?.playSequentially(anim1, anim2)
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!dismissed && popupWindow?.isShowing == true) {
                    animation.start()
                }
            }
        })
        animator?.start()
    }

    interface OnDismissListener {
        fun onDismiss(tooltip: SimpleTooltip)
    }

    interface OnShowListener {
        fun onShow(tooltip: SimpleTooltip)
    }
}