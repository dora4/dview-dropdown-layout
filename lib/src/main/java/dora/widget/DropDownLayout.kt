package dora.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import dora.widget.dropdownlayout.R

class DropDownLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnTouchListener {

    private var dropDownView: View? = null
    private var shadowLayer: View? = null
    private var shadowShown = false
    private var onShadowClickListener: OnShadowClickListener? = null

    fun setDropDownView(dropDownView: View): DropDownLayout {
        this.dropDownView = dropDownView
        return this
    }

    fun isShadowShown(): Boolean {
        return shadowShown
    }

    fun setOnShadowClickListener(l: OnShadowClickListener) {
        onShadowClickListener = l
    }

    fun showDropDownView() {
        dropDownView?.let {
            showShadowLayer()
            it.visibility = VISIBLE
        }
    }

    fun hideDropDownView() {
        dropDownView?.let {
            hideShadowLayer()
            it.visibility = GONE
        }
    }

    fun showShadowLayer() {
        if (!shadowShown && shadowLayer != null) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.anim_alpha_in)
            shadowLayer!!.startAnimation(animation)
            shadowLayer!!.visibility = VISIBLE
            shadowShown = true
        }
    }

    fun hideShadowLayer() {
        if (shadowShown && shadowLayer != null) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.anim_alpha_out)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    shadowLayer?.visibility = INVISIBLE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
            shadowLayer!!.startAnimation(animation)
            shadowShown = false
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            dropDownView = getChildAt(0)
            dropDownView?.visibility = GONE
        }
        addShadowLayer(context)
    }

    private fun addShadowLayer(context: Context) {
        shadowLayer = View(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(DEFAULT_SHADOW_COLOR)
            visibility = INVISIBLE
            setOnTouchListener(this@DropDownLayout)
        }
        addView(shadowLayer)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            shadowLayer?.let { onShadowClickListener?.onClickShadow(it) }
        }
        return true // 阴影层完全拦截
    }

    interface OnShadowClickListener {
        fun onClickShadow(shadowLayer: View)
    }

    companion object {
        const val DEFAULT_SHADOW_COLOR = 0x60000000
    }
}