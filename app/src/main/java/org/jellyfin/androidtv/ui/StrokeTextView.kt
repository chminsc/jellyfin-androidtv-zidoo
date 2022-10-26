package org.jellyfin.androidtv.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import org.jellyfin.androidtv.R

class StrokeTextView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {
	var strokeWidth: Float
	@ColorInt var strokeColor: Int
	private var isDrawing: Boolean = false

	init {
		context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView, defStyleAttr, 0).run {
			strokeWidth = getFloat(R.styleable.StrokeTextView_strokeWidth, 0.0f)
			strokeColor = getColor(R.styleable.StrokeTextView_strokeColor, Color.BLACK)
			recycle()
		}
	}

	override fun invalidate() {
		// To prevent infinite call of onDraw because setTextColor calls invalidate()
		if (isDrawing)
			return
		super.invalidate()
	}

	override fun onDraw(canvas: Canvas?) {
		if (strokeWidth <= 0)
			return super.onDraw(canvas)
		isDrawing = true
		val initialColor = textColors

		paint.style = Paint.Style.STROKE
		paint.strokeWidth = strokeWidth
		setTextColor(strokeColor)
		super.onDraw(canvas)

		paint.style = Paint.Style.FILL
		setTextColor(initialColor)
		super.onDraw(canvas)
		isDrawing = false
	}
}
