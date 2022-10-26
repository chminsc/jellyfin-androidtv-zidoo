package org.jellyfin.androidtv.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import me.carleslc.kotlin.extensions.number.roundDiv
import org.jellyfin.androidtv.R

class SpaceAbs @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val heightPctPx: Int?
    private val widthPctPx: Int?

    init {
        visibility = INVISIBLE

        context.obtainStyledAttributes(attrs, R.styleable.SpaceAbs, defStyleAttr, defStyleRes).run {
            heightPctPx = getFraction(R.styleable.SpaceAbs_heightPct, 1, 1, 0f).takeIf { it > 0 }?.let {
                // NOTE: make even
                (resources.displayMetrics.heightPixels * it).roundDiv(2).times(2).toInt()
            }
            widthPctPx = getFraction(R.styleable.SpaceAbs_widthPct, 1, 1, 0f).takeIf { it > 0 }?.let {
                // NOTE: make even
                (resources.displayMetrics.widthPixels * it).roundDiv(2).times(2).toInt()
            }
            recycle()
        }
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.apply {
            heightPctPx?.takeIf { height == 0 }?.let { height = heightPctPx }
            widthPctPx?.takeIf { width == 0 }?.let { width = widthPctPx }
        }
        super.setLayoutParams(params)
    }

    /**
     * Draw nothing.
     *
     * @param canvas an unused parameter.
     */
    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas?) {

    }

    /**
     * Compare to: [View.getDefaultSize]
     * If mode is AT_MOST, return the child size instead of the parent size
     * (unless it is too big).
     */
    private fun getDefaultSize2(size: Int, measureSpec: Int): Int {
        var result = size
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED -> result = size
            MeasureSpec.AT_MOST -> result = size.coerceAtMost(specSize)
            MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize2(suggestedMinimumWidth, widthMeasureSpec),
            getDefaultSize2(suggestedMinimumHeight, heightMeasureSpec)
        )
    }
}