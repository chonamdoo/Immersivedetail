package com.cho.immersivedetail.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Interpolator

/**
 * Created by chonamdoo on 2017. 9. 19..
 */


object LollipopCompatSingleton{
    private val INTERPOLATOR_FAST_OUT_SLOW_IN: Interpolator = FastOutSlowInInterpolator()

    private val DEFAULT_LOLLIPOP_STATUS_BAR_HEIGHT = 25.0f //dp

    private var statusBarHeight = -1
    private val ARGB_EVALUATOR = ArgbEvaluator()

    private fun getStatusBarHeightWhenLollipop21More(context: Context?): Int {
        if (statusBarHeight >= 0) {
            return statusBarHeight
        } else {
            if (!isLollipop21More()) {
                statusBarHeight = 0
                return statusBarHeight
            } else {
                var result = 0

                //第一次计算
                val resources = if (context != null) context.resources else Resources.getSystem()

                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    result = resources.getDimensionPixelSize(resourceId)
                }

                //第二次计算
                if (result <= 0) {
                    result = dp2px(context, DEFAULT_LOLLIPOP_STATUS_BAR_HEIGHT)
                }

                //汇总结果
                if (result >= 0) {
                    statusBarHeight = result
                    return statusBarHeight
                } else {
                    return 0
                }
            }
        }
    }

    fun translucentStatusBar(activity: Activity?) {
        if (activity != null && isLollipop21More()) {
            val window = activity.window
            if (window != null) {
                val view = window.decorView
                if (view != null) {
                    view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }

        }
    }

    /**
     * 注意: 不要持续的调这个方法, 调一次就够了.
     * @param activity
     * @param toColor
     * @param msec
     */
    @SuppressLint("NewApi")
    fun setStatusBarColorFade(activity: Activity?, toColor: Int, msec: Int) {
        if (activity == null || !isLollipop21More()) {
            return
        }
        val window = activity.window ?: return
        val statusBarColor = window.statusBarColor
        if (statusBarColor != toColor) {
            val statusBarColorAnim = ValueAnimator.ofObject(ARGB_EVALUATOR, statusBarColor, toColor)
            statusBarColorAnim.addUpdateListener { animation ->
                window.statusBarColor = animation
                        .animatedValue as Int
            }
            statusBarColorAnim.duration = msec.toLong()
            statusBarColorAnim.interpolator = INTERPOLATOR_FAST_OUT_SLOW_IN
            statusBarColorAnim.start()
        }
    }

    @SuppressLint("NewApi")
    fun setStatusBarColorImmediately(activity: Activity?, color: Int) {
        if (!isLollipop21More() || activity == null) {
            return
        }
        val window = activity.window
        if (window != null) {
            if (window.statusBarColor != color) {
                window.statusBarColor = color
            }
        }
    }

    fun fitStatusBarTranslucentPadding(view: View?, context: Context) {
        view?.setPadding(0, getStatusBarHeightWhenLollipop21More(context), 0, 0)
    }


    private fun dp2px(context: Context?, dpValue: Float): Int {
        var scale = context?.resources?.displayMetrics?.density ?: Resources.getSystem().displayMetrics.density
        scale = if (scale > 0) scale else DisplayMetrics.DENSITY_DEFAULT.toFloat()
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun isLollipop21More(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
}