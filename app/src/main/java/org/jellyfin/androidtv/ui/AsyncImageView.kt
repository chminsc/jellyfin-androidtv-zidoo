package org.jellyfin.androidtv.ui

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.*
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.vanniktech.blurhash.BlurHash
import kotlinx.coroutines.*
import me.carleslc.kotlin.extensions.standard.*
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.AsyncImageView.CropTypeGlide.Companion.applyCropOption
import org.jellyfin.androidtv.util.ImageHelper
import org.jellyfin.androidtv.util.ImageUtils.*
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds

/**
 * An extension to the [ImageView] that makes it easy to load images from the network.
 * The [load] function takes a url, blurhash and placeholder to asynchronously load the image
 * using the lifecycle of the current fragment or activity.
 */
class AsyncImageView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {
	private val lifeCycleOwner get() = findViewTreeLifecycleOwner()
	private val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.AsyncImageView, defStyleAttr, 0)

	/**
	 * The duration of the crossfade when changing switching the images of the url, blurhash and
	 * placeholder.
	 */
	@Suppress("MagicNumber")
	var crossFadeDuration = styledAttributes.getInt(R.styleable.AsyncImageView_crossfadeDuration, 200).milliseconds

	var scaleTypeGlide = ScaleTypeGlide.from(styledAttributes.getInt(R.styleable.AsyncImageView_scaleTypeGlide, ScaleTypeGlide.DEFAULT.value))

	var scaleTypeGlideFallback = ScaleTypeGlide.from(styledAttributes.getInt(R.styleable.AsyncImageView_scaleTypeGlide, ScaleTypeGlide.DEFAULT.value))

	var cropTypeGlide = CropTypeGlide.from(styledAttributes.getInt(R.styleable.AsyncImageView_cropTypeGlide, CropTypeGlide.NONE.value))

	/**
	 * Load an image from the network using [url]. When the [url] is null or returns a bad response
	 * the [placeholder] is shown. A [blurHash] is shown while loading the image. An aspect ratio is
	 * required when using a BlurHash or the sizing will be incorrect.
	 */
	fun load(
		url: String?,
		blurHash: String? = null,
		fallback: Drawable? = null,
		blurAspectRatio: Double = 1.0,
		blurResolution: Int = 32,
	) {
		if (url.isNullOrBlank()) {
			if (fallback.isNotNull())
				this@AsyncImageView.setImageDrawable(fallback!!)
			return
		}
		doOnAttach {
			lifeCycleOwner?.lifecycleScope?.launch {
			val checkedUrl = ImageHelper.checkImageUrl(url)

//			Timber.d("Url: $checkedUrl")
//			Timber.d("GlideLoad m<$measuredHeight:$measuredWidth> : <$height:$width>)")
				// Start loading image or placeholder
				Glide.with(this@AsyncImageView)
					.load(checkedUrl)
					.downsample(scaleTypeGlide.toDownsampleStrategy())
					.applyCropOption(cropTypeGlide)
					.into(object : CustomViewTarget<AsyncImageView, Drawable>(this@AsyncImageView) {
						override fun onLoadFailed(errorDrawable: Drawable?) {
							this@AsyncImageView.setImageDrawable(errorDrawable)
						}

						override fun onResourceReady(newImage: Drawable, transition: Transition<in Drawable>?) {
							this@AsyncImageView.setImageDrawable(newImage)
						}

						override fun onResourceCleared(placeholder: Drawable?) {
							this@AsyncImageView.setImageDrawable(placeholder)
						}

						override fun onResourceLoading(placeholder: Drawable?) {
							blurHash?.let {
								generateBlurImage(it, blurAspectRatio, blurResolution)
							}?.let {
								this@AsyncImageView.setImageDrawable(it)
							}
						}
					}).waitForLayout()

				// FIXME: Glide is unable to scale the image when transitions are enabled
				//.transition(DrawableTransitionOptions.withCrossFade(crossFadeDuration.inWholeMilliseconds.toInt()))
			}
		}
	}

	private fun generateBlurImage(
		blurHash: String,
		blurAspectRatio: Double = 1.0,
		blurResolution: Int = 32,
	): BitmapDrawable? {
		return BlurHash.decode(
			blurHash,
			if (blurAspectRatio > 1) round(blurResolution * blurAspectRatio).toInt() else blurResolution,
			if (blurAspectRatio >= 1) blurResolution else round(blurResolution / blurAspectRatio).toInt(),
		)?.toDrawable(resources)
	}

	enum class ScaleTypeGlide (val value: Int) {
		NONE(0),
		DEFAULT(1),
		AT_LEAST(2),
		AT_MOST(3),
		FIT_CENTER(4),
		CENTER_INSIDE(5),
		CENTER_OUTSIDE(6);

		fun toDownsampleStrategy(): DownsampleStrategy = when (this) {
			NONE -> DownsampleStrategy.NONE
			DEFAULT -> DownsampleStrategy.DEFAULT
			AT_LEAST -> DownsampleStrategy.AT_LEAST
			AT_MOST -> DownsampleStrategy.AT_MOST
			FIT_CENTER -> DownsampleStrategy.FIT_CENTER
			CENTER_INSIDE -> DownsampleStrategy.CENTER_INSIDE
			CENTER_OUTSIDE -> DownsampleStrategy.CENTER_OUTSIDE
		}

		companion object {
			fun from(intValue: Int): ScaleTypeGlide = values().find { it.value == intValue } ?: DEFAULT
		}
	}

	enum class CropTypeGlide(val value: Int) {
		NONE(0),
		CENTER_CROP(1),
		CIRCLE_CROP(2);

		companion object {
			fun from(intValue: Int): CropTypeGlide = values().first { it.value == intValue }

			fun <T> RequestBuilder<T>.applyCropOption(value: CropTypeGlide) = when (value) {
				CENTER_CROP -> this.centerCrop()
				CIRCLE_CROP -> this.circleCrop()
				else -> this
			}
		}
	}
}
