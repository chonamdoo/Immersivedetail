package com.cho.immersivedetail.widget

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.cho.immersivedetail.R
import com.cho.immersivedetail.util.LollipopCompatSingleton
import java.lang.ref.WeakReference

/**
 * Created by chonamdoo on 2017. 9. 19..
 */

class ObservableScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ScrollView(context, attrs,defStyle){
    private val TOOLBAR_STATE_NORMAL = 0
    private val TOOLBAR_STATE_TRANSPARENT = 1

    private lateinit var activityWeakReference: WeakReference<Activity>
    private lateinit var imageHeaderContainer: View
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitleView: TextView

    private var toolbarColor: Int = 0
    private var toolbarState = TOOLBAR_STATE_NORMAL
    private var defaultToolbarColor = Color.BLACK
    private var oldScrollY = 0
    private var lastScrollYDirection = 0

    private var isImmersiveEffectOpen: Boolean = false

    private var scrollViewListener: ScrollViewListener? = null

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it,R.styleable.immersive_detail, 0, 0)
            defaultToolbarColor = typedArray.getColor(R.styleable.immersive_detail_toolbar_color,
                    Color.BLACK)
            typedArray.recycle()
        }
    }
    fun setScrollViewListener(scrollViewListener: ScrollViewListener) {
        this.scrollViewListener = scrollViewListener
    }

    fun setupImmersiveEffect(activity: Activity, imageHeaderContainer: View,
                             toolbar: Toolbar, toolbarColor: Int, toolbarTitleView: TextView) {
        activityWeakReference = WeakReference(activity)
        this.imageHeaderContainer = imageHeaderContainer
        this.toolbar = toolbar
        this.toolbarColor = toolbarColor
        this.toolbarTitleView = toolbarTitleView
        if (activity != null && imageHeaderContainer != null && toolbar != null && toolbarTitleView != null) {
            isImmersiveEffectOpen = true
            toolbarTitleView.visibility = View.INVISIBLE
            setToolbarColor(TOOLBAR_STATE_TRANSPARENT)
        }
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        applyScrollControlToolbar(y)
        scrollViewListener?.let {
            it.onScrollChanged(this, x, y, oldx, oldy)
        }
    }

    private fun applyScrollControlToolbar(scrollY: Int) {
        if (!isImmersiveEffectOpen) {
            return
        }
        imageHeaderContainer.translationY = scrollY * 0.5f

        if (scrollY - getFlexibleSpace() < toolbar.height && toolbarState == TOOLBAR_STATE_TRANSPARENT) {
            val y = Math.min(0, -scrollY + getFlexibleSpace()).toFloat()
            if (y < -1) {
                LollipopCompatSingleton.setStatusBarColorFade(activityWeakReference.get(),defaultToolbarColor
                        /*ContextCompat.getColor(context, R.color.colorPrimaryDark)*/, 300)
            } else if (y == 0f) {
                LollipopCompatSingleton.setStatusBarColorImmediately(activityWeakReference.get(),
                        ContextCompat.getColor(context, android.R.color.transparent))
            }
            toolbar.translationY = y
        }


        if (scrollY >= imageHeaderContainer.height && isScrollDown(scrollY) && toolbarState != TOOLBAR_STATE_NORMAL) {
            setToolbarColor(TOOLBAR_STATE_NORMAL)
            toolbar.visibility = View.INVISIBLE
            toolbar.translationY = (-toolbar.height).toFloat()
            toolbar.visibility = View.VISIBLE
        }

        if (isScrollDown(scrollY) && toolbarState == TOOLBAR_STATE_NORMAL && toolbar.translationY < 0 && scrollY - oldScrollY != 0) {
            if (toolbar.translationY + Math.abs(scrollY - oldScrollY) <= 0) {
                toolbar.translationY = toolbar.translationY + Math.abs(scrollY - oldScrollY)
            } else {
                toolbar.translationY = 0f
            }
        }

        if (isScrollUp(scrollY) && toolbarState == TOOLBAR_STATE_NORMAL && toolbar.translationY <= 0 && scrollY - oldScrollY != 0) {
            if (toolbar.translationY - Math.abs(scrollY - oldScrollY) > -toolbar.height) {
                toolbar.translationY = toolbar.translationY - Math.abs(scrollY - oldScrollY)
            } else {
                toolbar.translationY = (-toolbar.height).toFloat()
            }
        }

        if (imageHeaderContainer.translationY * 2 <= getFlexibleSpace() && toolbarState == TOOLBAR_STATE_NORMAL) {
            val colorFade = ObjectAnimator.ofObject(toolbar, "backgroundColor", ArgbEvaluator(), toolbarColor, android.R.color.transparent)
            colorFade.duration = 300
            colorFade.start()
            Handler().postDelayed({ toolbarTitleView.visibility = View.INVISIBLE }, 150)
            toolbarState = TOOLBAR_STATE_TRANSPARENT
        }

        setScrollDirections(scrollY)

    }

    fun setToolbarColor(state: Int) {
        if (toolbarState != state) {
            toolbar.setBackgroundColor(if (state == TOOLBAR_STATE_TRANSPARENT) ContextCompat.getColor(context, android.R.color.transparent) else toolbarColor)
            toolbarState = state
        }

        if (state == TOOLBAR_STATE_NORMAL) {
            toolbarTitleView.visibility = View.VISIBLE
        } else if (state == TOOLBAR_STATE_TRANSPARENT) {
            toolbarTitleView.visibility = View.INVISIBLE
        }

    }

    private fun isScrollDown(scrollY: Int): Boolean {
        return scrollY <= oldScrollY && lastScrollYDirection == -1
    }

    private fun isScrollUp(scrollY: Int): Boolean {
        return scrollY >= oldScrollY && lastScrollYDirection == 1
    }

    fun getFlexibleSpace(): Int {
        return imageHeaderContainer.height - toolbar.height
    }

    private fun setScrollDirections(scrollY: Int) {
        if (scrollY > oldScrollY)
            lastScrollYDirection = 1
        if (scrollY < oldScrollY)
            lastScrollYDirection = -1

        oldScrollY = scrollY
    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
    }

}