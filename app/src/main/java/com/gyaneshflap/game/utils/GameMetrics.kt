package com.gyaneshflap.game.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

/**
 * Standardized Game Metric System:
 * Converts DP/SP to exact pixels dynamically and provides relative screen scaling
 * ensuring consistent visual proportion across all Android screens and densities.
 */
class GameMetrics(context: Context) {

    val density: Float = context.resources.displayMetrics.density
    val scaledDensity: Float = context.resources.displayMetrics.scaledDensity
    val widthPixels: Int = context.resources.displayMetrics.widthPixels
    val heightPixels: Int = context.resources.displayMetrics.heightPixels

    // DP to PX conversion
    fun dp(dpVal: Float): Float = dpVal * density

    // SP to PX conversion for text
    fun sp(spVal: Float): Float = spVal * scaledDensity

    // Percentage of screen width
    fun widthPercent(percent: Float): Float = widthPixels * (percent / 100f)

    // Percentage of screen height
    fun heightPercent(percent: Float): Float = heightPixels * (percent / 100f)

    companion object {
        fun dpToPx(dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                Resources.getSystem().displayMetrics
            )
        }

        fun spToPx(sp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                Resources.getSystem().displayMetrics
            )
        }
    }
}
