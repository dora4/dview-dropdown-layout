package dora.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import dora.widget.dropdownlayout.R

open class DropDownLayout @JvmOverloads constructor(
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
        if (dropDownView != null) {
            showShadowLayer()
            addView(dropDownView)
        }
    }

    fun hideDropDownView() {
        if (dropDownView != null) {
            hideShadowLayer()
            removeView(dropDownView)
        }
    }

    open fun showShadowLayer() {
        if (!shadowShown && shadowLayer != null) {
            shadowLayer!!.visibility = VISIBLE
            shadowLayer!!.animation =
                AnimationUtils.loadAnimation(context, R.anim.anim_alpha_in)
            shadowShown = true
        }
    }

    open fun hideShadowLayer() {
        if (shadowShown && shadowLayer != null) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.anim_alpha_out)
            shadowLayer!!.animation = animation
            shadowLayer!!.visibility = INVISIBLE
            shadowShown = false
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addShadowLayer(context)
    }

    private fun addShadowLayer(context: Context) {
        shadowLayer = View(context)
        shadowLayer!!.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        shadowLayer!!.setBackgroundColor(DEFAULT_SHADOW_COLOR)
        shadowLayer!!.visibility = INVISIBLE
        shadowLayer!!.setOnTouchListener(this)
        addView(shadowLayer)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (shadowLayer != null) {
            onShadowClickListener?.onClickShadow(shadowLayer!!)
        }
        return true
    }

    interface OnShadowClickListener {
        fun onClickShadow(shadowLayer: View)
    }

    companion object {
        const val DEFAULT_SHADOW_COLOR = 0x60000000
    }
}