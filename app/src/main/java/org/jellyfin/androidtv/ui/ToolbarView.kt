package org.jellyfin.androidtv.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.databinding.ViewToolbarBinding

class ToolbarView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
	defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

	val binding: ViewToolbarBinding = ViewToolbarBinding.inflate(LayoutInflater.from(context), this, true)

	init {
		layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.TOP or Gravity.END).apply {
			this.topMargin = resources.getDimensionPixelSize(R.dimen.safe_area_vertical)
			this.marginEnd = resources.getDimensionPixelSize(R.dimen.safe_area_horizontal)
		}
		elevation = resources.getDimension(R.dimen.toolbar_elevation)
	}

	fun addButton(@DrawableRes imageResource: Int, @StringRes contentDescription: Int): ImageButton {
		return addButton(imageResource, contentDescription, null)
	}

	fun addButton(@DrawableRes imageResource: Int, @StringRes contentDescription: Int, onClickListener: OnClickListener?) =
		ImageButton(context, null, 0, R.style.Button_Icon_Toolbar).apply {
			layoutParams = LayoutParams(binding.toolbarContent.layoutParams.width, binding.toolbarContent.layoutParams.height)
			setImageResource(imageResource)
			setContentDescription(resources.getString(contentDescription))
			onClickListener?.let { x -> setOnClickListener(x) }
		}.also {
			binding.toolbarContent.addView(it)
		}
}
