package org.jellyfin.androidtv.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import org.jellyfin.androidtv.JellyfinApplication
import kotlin.math.roundToInt

/**
 * Convert Dp to Pixel
 */
fun Int.dp(context: Context): Int = (context.resources.displayMetrics.density * this).roundToInt()
fun Int.dp(): Int = (JellyfinApplication.appContext.resources.displayMetrics.density * this).roundToInt()

/**
 * Convert Sp to Pixel
 */
fun Int.sp(context: Context): Int = (context.resources.displayMetrics.scaledDensity * this).roundToInt()
fun Int.sp(): Int = (JellyfinApplication.appContext.resources.displayMetrics.scaledDensity * this).roundToInt()

fun Int.msToRuntimeTicks(): Long = (this * 10000).coerceAtLeast(0).toLong()
fun Long.runtimeTicksToMs(): Int = (this / 10000).coerceAtLeast(0).toInt()

@ColorInt
fun @receiver:ColorInt Int.saturationHSV(@FloatRange(from = 0.0, to = 1.0) sValue: Float): Int = FloatArray(3).let {
    Color.colorToHSV(this, it)
    it[1] = sValue
    Color.HSVToColor(it)
}

@ColorInt
fun @receiver:ColorInt Int.valueHSV(@FloatRange(from = 0.0, to = 1.0) vValue: Float): Int = FloatArray(3).let {
    Color.colorToHSV(this, it)
    it[2] = vValue
    Color.HSVToColor(it)
}