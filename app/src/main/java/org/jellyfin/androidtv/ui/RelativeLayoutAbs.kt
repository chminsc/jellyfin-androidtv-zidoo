package org.jellyfin.androidtv.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import me.carleslc.kotlin.extensions.number.roundDiv
import org.jellyfin.androidtv.R

class RelativeLayoutAbs @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val heightPctPx: Int?
    private val widthPctPx: Int?

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RelativeLayoutAbs, defStyleAttr, defStyleRes).run {
            heightPctPx = getFraction(R.styleable.RelativeLayoutAbs_heightPct, 1, 1, 0f).takeIf { it > 0 }?.let {
                // NOTE: make even
                (resources.displayMetrics.heightPixels * it).roundDiv(2).times(2).toInt()
            }
            widthPctPx = getFraction(R.styleable.RelativeLayoutAbs_widthPct, 1, 1, 0f).takeIf { it > 0 }?.let {
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
}