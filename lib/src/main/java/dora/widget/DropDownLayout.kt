package dora.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import dora.widget.dropdownlayout.R

class DropDownLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var dropDownView: View? = null
    private var shadowLayer: View? = null
    private var shadowColor: Int = DEFAULT_SHADOW_COLOR
    private var shadowShown = false
    private var onShadowClickListener: OnShadowClickListener? = null

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.DropDownLayout)
            shadowColor = a.getColor(R.styleable.DropDownLayout_dview_ddl_shadowColor, shadowColor)
            a.recycle()
        }
    }

    fun setDropDownView(dropDownView: View): DropDownLayout {
        dropDownView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)    // 防止LayoutInflater加载时不会设置LayoutParams
        dropDownView.setOnTouchListener { v, event -> true }    // 防止事件透传到阴影层
        this.dropDownView = dropDownView
        addShadowLayer(context)
        dropDownView.visibility = View.INVISIBLE
        addView(dropDownView)
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
            showShadowLayer {
                it.visibility = VISIBLE
            }
        }
    }

    fun hideDropDownView() {
        dropDownView?.let {
            hideShadowLayer {
                it.visibility = INVISIBLE
            }
        }
    }

    private fun showShadowLayer(callback: () -> Unit) {
        if (!shadowShown && shadowLayer != null) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.dview_anim_alpha_in)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    shadowLayer!!.visibility = VISIBLE
                    shadowShown = true
                    callback()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
            shadowLayer!!.startAnimation(animation)
        }
    }

    private fun hideShadowLayer(callback: () -> Unit) {
        if (shadowShown && shadowLayer != null) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.dview_anim_alpha_out)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    shadowLayer?.visibility = INVISIBLE
                    shadowShown = false
                    callback()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
            shadowLayer!!.startAnimation(animation)
        }
    }

    private fun addShadowLayer(context: Context) {
        shadowLayer = View(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(shadowColor)
            visibility = INVISIBLE
        }
        shadowLayer?.let {
            it.setOnClickListener {
                onShadowClickListener?.onClickShadow(it)
            }
        }
        addView(shadowLayer)
    }

    interface OnShadowClickListener {
        fun onClickShadow(shadowLayer: View)
    }

    companion object {
        const val DEFAULT_SHADOW_COLOR = 0x60000000
    }
}