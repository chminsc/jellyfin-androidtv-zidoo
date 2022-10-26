package org.jellyfin.androidtv.ui.preference.screen

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.constant.CardInfoType
import org.jellyfin.androidtv.constant.HomeSectionType
import org.jellyfin.androidtv.constant.HomeSize
import org.jellyfin.androidtv.preference.UserSettingPreferences
import org.jellyfin.androidtv.preference.UserSettingPreferences.Companion.cardInfoType
import org.jellyfin.androidtv.preference.UserSettingPreferences.Companion.homeSize
import org.jellyfin.androidtv.preference.UserSettingPreferences.Companion.seriesThumbnailsEnabled
import org.jellyfin.androidtv.ui.preference.dsl.OptionsFragment
import org.jellyfin.androidtv.ui.preference.dsl.checkbox
import org.jellyfin.androidtv.ui.preference.dsl.enum
import org.jellyfin.androidtv.ui.preference.dsl.optionsScreen
import org.jellyfin.preference.store.PreferenceStore
import org.koin.android.ext.android.inject

class HomePreferencesScreen : OptionsFragment() {
	private val userSettingPreferences: UserSettingPreferences by inject()

	override val stores: Array<PreferenceStore<*, *>>
		get() = arrayOf(userSettingPreferences)

	override val screen by optionsScreen {
		setTitle(R.string.home_prefs)

		category {
			setTitle(R.string.home_section_settings)

			enum<HomeSize> {
				title = getString(R.string.pref_home_size)
				bind(userSettingPreferences, homeSize)
			}

			checkbox {
				setTitle(R.string.lbl_use_series_thumbnails)
				setContent(R.string.lbl_use_series_thumbnails_description)
				bind(userSettingPreferences, seriesThumbnailsEnabled)
			}

			enum<CardInfoType> {
				title = getString(R.string.lbl_card_info_style)
				bind(userSettingPreferences, cardInfoType)
			}
		}

		category {
			setTitle(R.string.home_sections)

			arrayOf(
				UserSettingPreferences.homesection0,
				UserSettingPreferences.homesection1,
				UserSettingPreferences.homesection2,
				UserSettingPreferences.homesection3,
				UserSettingPreferences.homesection4,
				UserSettingPreferences.homesection5,
				UserSettingPreferences.homesection6,
			).forEachIndexed { index, section ->
				enum<HomeSectionType> {
					title = getString(R.string.home_section_i, index + 1)
					bind(userSettingPreferences, section)
				}
			}
		}
	}
}
