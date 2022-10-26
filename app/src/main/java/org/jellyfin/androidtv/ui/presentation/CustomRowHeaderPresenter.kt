package org.jellyfin.androidtv.ui.presentation

import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.util.setAutoTextSizeSingleLine

class CustomRowHeaderPresenter constructor(
	var headerHeightPx: Int,
	var headerPaddingPx: Int
) : RowHeaderPresenter() {

	override fun onCreateViewHolder(parent: ViewGroup?): Presenter.ViewHolder {
		return super.onCreateViewHolder(parent).apply {
			view.layoutParams?.apply {
				height = headerHeightPx
			}
			view.findViewById<TextView>(R.id.row_header)?.apply {
				setAutoTextSizeSingleLine(headerHeightPx - headerPaddingPx)
			}
		}
	}
}
