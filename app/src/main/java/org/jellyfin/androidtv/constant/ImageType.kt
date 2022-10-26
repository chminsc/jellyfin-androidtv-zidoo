package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions
import org.jellyfin.androidtv.util.ImageUtils

enum class ImageType {
	/**
	 * Poster.
	 */
	@EnumDisplayOptions(R.string.image_type_poster)
	POSTER,

	/**
	 * Thumbnail.
	 */
	@EnumDisplayOptions(R.string.image_type_thumbnail)
	THUMB,

	/**
	 * Banner.
	 */
	@EnumDisplayOptions(R.string.image_type_banner)
	BANNER;

	fun getAspect(): Double = when(this) {
		POSTER -> ImageUtils.ASPECT_RATIO_POSTER
		THUMB -> ImageUtils.ASPECT_RATIO_THUMB
		BANNER -> ImageUtils.ASPECT_RATIO_BANNER
	}
}
