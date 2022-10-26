package org.jellyfin.androidtv.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.*
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import org.jellyfin.androidtv.JellyfinApplication
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.constant.FocusBorderSize

object DrawableBuilder {
    fun createFocusBorderDrawable(focusSize: FocusBorderSize,
                                  @ColorInt color: Int = Color.LTGRAY,
                                  cornerRadius: Int? = null,
                                  squareBottom: Boolean = false
    ) = StateListDrawable().apply {
        PaintDrawable().apply {
            paint.style = Paint.Style.STROKE
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeWidth = focusSize.sizeDP * JellyfinApplication.appContext.resources.displayMetrics.density
            paint.color = color
            paint.isAntiAlias = true
            cornerRadius?.toFloat()?.let {
                if (squareBottom)
                    setCornerRadii(floatArrayOf(it, it, it, it, it / 1.5f, it / 1.5f, it / 1.5f, it / 1.5f)) // make bottom corners smaller
                else
                    setCornerRadius(it)
            }
        }.also {
            addState(intArrayOf(android.R.attr.state_focused, android.R.attr.state_activated), it)
        }
    }

    fun createRainbowDrawable() = GradientDrawable().apply {
        orientation = GradientDrawable.Orientation.LEFT_RIGHT
        colors = intArrayOf(Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED)
    }

    fun createFocusProgressDrawable(context: Context, @ColorInt strokeColor: Int): Drawable? {
        val progress = AppCompatResources.getDrawable(context, R.drawable.progress_bar_card_focus)?.mutate() as? LayerDrawable
        val bgLayer = progress?.findDrawableByLayerId(android.R.id.background) as? StateListDrawable
        bgLayer?.apply {
            GradientDrawable().apply {
                setStroke(1, strokeColor)
                color = ColorStateList.valueOf(context.getColorFromAttribute(R.attr.progressBackgroundFocus))
            }.also {
                addState(intArrayOf(android.R.attr.state_focused, android.R.attr.state_activated), it)
            }
        }

        return progress ?: AppCompatResources.getDrawable(context, R.drawable.progress_bar_card_focus)
    }
}