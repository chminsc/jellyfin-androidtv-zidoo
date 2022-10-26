package org.jellyfin.androidtv.util

import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView

fun TextView.setAutoTextSizeSingleLine(heightPx: Int? = null) = apply {
    val height = heightPx ?: layoutParams?.height ?: measuredHeight
    height.takeIf { it > 0 }?.let {
        // text always has a smaller baseline
        ellipsize = TextUtils.TruncateAt.END
        includeFontPadding = false
        maxLines = 1
        setTextSize(TypedValue.COMPLEX_UNIT_PX, it.minus(it * 0.15f).coerceAtLeast(12f))
    }
}

fun TextView.setDefaultShadow() = apply {
    setShadowLayer(5f, 1f,1f, Color.BLACK)
}

fun TextView.setShadow(radius: Float) = apply {
    setShadowLayer(radius, 1f,1f, Color.BLACK)
}