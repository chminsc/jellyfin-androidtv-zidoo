package org.jellyfin.androidtv.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import me.carleslc.kotlinextensions.number.roundDiv
import org.jellyfin.androidtv.R

class TextViewAbs @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val heightPctPx: Int?
    private val widthPctPx: Int?

    init {
        context.obtainStyledAttributes(attrs, R.styleable.TextViewAbs, defStyleAttr, 0).run {
            heightPctPx = getFraction(R.styleable.TextViewAbs_heightPct, 1, 1, 0f).takeIf { it > 0 }?.let {
                // NOTE: make even
                (resources.displayMetrics.heightPixels * it).roundDiv(2).times(2).toInt()
            }
            widthPctPx = getFraction(R.styleable.TextViewAbs_widthPct, 1, 1, 0f).takeIf { it > 0 }?.let {
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