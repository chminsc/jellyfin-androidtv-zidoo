package org.jellyfin.androidtv.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.auth.repository.UserRepository
import org.jellyfin.androidtv.databinding.ClockUserBugBinding
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.preference.constant.ClockBehavior
import org.jellyfin.androidtv.ui.playback.PlaybackOverlayActivity
import org.jellyfin.androidtv.util.ImageUtils
import org.jellyfin.androidtv.util.getActivity
import org.jellyfin.androidtv.util.sp
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClockUserView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
	defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), KoinComponent {
	private val binding: ClockUserBugBinding = ClockUserBugBinding.inflate(LayoutInflater.from(context), this, true)
	private val userPreferences by inject<UserPreferences>()
	private val userRepository by inject<UserRepository>()

	init {
		layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)

		if (!isInEditMode) {
			val showClock = userPreferences[UserPreferences.clockBehavior]

			binding.clock.isVisible = when (showClock) {
				ClockBehavior.ALWAYS -> true
				ClockBehavior.NEVER -> false
				ClockBehavior.IN_VIDEO -> context.getActivity() is PlaybackOverlayActivity
				ClockBehavior.IN_MENUS -> context.getActivity() !is PlaybackOverlayActivity
			}

			val currentUser = userRepository.currentUser.value

			binding.clockUserImage.load(
				url = currentUser?.let(ImageUtils::getPrimaryImageUrl),
				fallback = ContextCompat.getDrawable(context, R.drawable.ic_user)
			)

			binding.clockUserImage.isVisible = currentUser != null
		}
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		getDefaultSize(20.sp(context), heightMeasureSpec).let {
			binding.clock.setTextSize(TypedValue.COMPLEX_UNIT_PX, (it - 8).coerceAtLeast(9.sp(context)).toFloat() )
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	}
}
