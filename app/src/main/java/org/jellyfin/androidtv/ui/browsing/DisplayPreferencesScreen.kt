package org.jellyfin.androidtv.ui.browsing

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.constant.CardInfoType
import org.jellyfin.androidtv.constant.CardSpacing
import org.jellyfin.androidtv.constant.GridDirection
import org.jellyfin.androidtv.constant.ImageType
import org.jellyfin.androidtv.preference.LibraryPreferences
import org.jellyfin.androidtv.preference.PreferencesRepository
import org.jellyfin.androidtv.preference.constant.LanguagesAudio
import org.jellyfin.androidtv.preference.constant.RatingType
import org.jellyfin.androidtv.ui.preference.custom.DurationSeekBarPreference
import org.jellyfin.androidtv.ui.preference.dsl.*
import org.jellyfin.preference.store.PreferenceStore
import org.koin.android.ext.android.inject

class DisplayPreferencesScreen : OptionsFragment() {
	private val preferencesRepository: PreferencesRepository by inject()
	private val libraryPreferences: LibraryPreferences by lazy {
		preferencesRepository.getLibraryPreferences(preferencesId!!)
	}

	private val preferencesId by lazy { requireArguments().getString(ARG_PREFERENCES_ID) }
	private val allowViewSelection by lazy { requireArguments().getBoolean(ARG_ALLOW_VIEW_SELECTION) }

	override val stores: Array<PreferenceStore<*, *>>
		get() = arrayOf(libraryPreferences)

	override val screen by optionsScreen {
		setTitle(R.string.lbl_library_preferences)

		category {
			setTitle(R.string.lbl_library_general_preferences)

			if (allowViewSelection) {
				checkbox {
					setTitle(R.string.enable_smart_view)
					contentOn = requireContext().getString(R.string.enable_smart_view_description)
					contentOff = contentOn

					bind(libraryPreferences, LibraryPreferences.enableSmartScreen)
				}
			}
		}

		category {
			setTitle(R.string.lbl_display_preferences)

			enum<ImageType> {
				setTitle(R.string.lbl_image_type)
				bind(libraryPreferences, LibraryPreferences.imageType)
			}

			seekbar {
				setTitle(R.string.lbl_grid_size)
				min = LibraryPreferences.getGridSizeChecked(1, libraryPreferences[LibraryPreferences.gridDirection], libraryPreferences[LibraryPreferences.imageType])
				max = LibraryPreferences.getGridSizeChecked(30, libraryPreferences[LibraryPreferences.gridDirection], libraryPreferences[LibraryPreferences.imageType])
				increment = 1
				valueFormatter = object : DurationSeekBarPreference.ValueFormatter() {
					override fun display(value: Int): String {
						return LibraryPreferences.getGridSizeChecked(value, libraryPreferences[LibraryPreferences.gridDirection], libraryPreferences[LibraryPreferences.imageType]).toString()
					}
				}
				bind(libraryPreferences, LibraryPreferences.gridSize)
			}

			enum<GridDirection> {
				setTitle(R.string.grid_direction)
				bind(libraryPreferences, LibraryPreferences.gridDirection)
			}

			enum<CardInfoType> {
				setTitle(R.string.pref_card_infotype)
				bind(libraryPreferences, LibraryPreferences.cardInfoType)
			}

			enum<CardSpacing> {
				setTitle(R.string.pref_card_spacing)
				bind(libraryPreferences, LibraryPreferences.cardSpacing)
			}

			enum<RatingType> {
				setTitle(R.string.pref_default_rating)
				bind(libraryPreferences, LibraryPreferences.ratingType)
			}
		}

		category {
			setTitle(R.string.pref_audio)

			checkbox {
				setTitle(R.string.lbl_enable_library_audio_settings)
				setContent(R.string.desc_enable_library_audio_settings)
				bind(libraryPreferences, LibraryPreferences.enableAudioSettings)
			}

			enum<LanguagesAudio> {
				setTitle(R.string.pref_languages_audio)
				bind(libraryPreferences, LibraryPreferences.audioLanguage)
				depends { libraryPreferences[LibraryPreferences.enableAudioSettings] }
			}
		}
	}

	companion object {
		const val ARG_ALLOW_VIEW_SELECTION = "allow_view_selection"
		const val ARG_PREFERENCES_ID = "preferences_id"
	}
}
