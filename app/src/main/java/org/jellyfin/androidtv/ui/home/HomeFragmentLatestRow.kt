package org.jellyfin.androidtv.ui.home

import android.content.Context
import androidx.leanback.widget.ArrayObjectAdapter
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.auth.repository.UserRepository
import org.jellyfin.androidtv.constant.ImageType
import org.jellyfin.androidtv.preference.UserSettingPreferences
import org.jellyfin.androidtv.ui.browsing.BrowseRowDef
import org.jellyfin.androidtv.ui.presentation.CardPresenter
import org.jellyfin.apiclient.model.querying.ItemFields
import org.jellyfin.apiclient.model.querying.ItemsResult
import org.jellyfin.apiclient.model.querying.LatestItemsQuery
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.constant.CollectionType

class HomeFragmentLatestRow(
	private val context: Context,
	private val userRepository: UserRepository,
	private val userSettingPreferences: UserSettingPreferences,
	private val views: ItemsResult
) : HomeFragmentRow {
	override fun addToRowsAdapter(context: Context, presenter: CardPresenter, rowsAdapter: ArrayObjectAdapter) {
		// Get configuration (to find excluded items)
		val configuration = userRepository.currentUser.value?.configuration

		// Create a list of views to include
		val latestItemsExcludes = configuration?.latestItemsExcludes.orEmpty()
		views.items
			.filterNot { item -> item.collectionType in EXCLUDED_COLLECTION_TYPES || item.id in latestItemsExcludes }
			.map { item ->
				// Create query and add it to a new row
				val query = LatestItemsQuery().apply {
					fields = arrayOf(
						ItemFields.PrimaryImageAspectRatio,
						ItemFields.Overview,
						ItemFields.ChildCount,
						ItemFields.DateLastMediaAdded,
						ItemFields.DateCreated
					)
					imageTypeLimit = 1
					parentId = item.id
					groupItems = true
					limit = ITEM_LIMIT
					includeItemTypes = arrayOf(
						BaseItemKind.EPISODE.serialName,
						BaseItemKind.MOVIE.serialName,
//						BaseItemKind.SERIES.serialName,
						BaseItemKind.MUSIC_ALBUM.serialName,
//						BaseItemKind.RECORDING.serialName,
						BaseItemKind.AUDIO_BOOK.serialName,
					)
				}

				val title = String.format("%s %s", context.getString(R.string.lbl_latest), item.name)
				HomeFragmentBrowseRowDefRow(
					BrowseRowDef(title, query, HomeFragmentHelper.DEFAULT_TRIGGERS).setFilterPlayed(),
					cardInfoType = userSettingPreferences[UserSettingPreferences.cardInfoType],
					cardImageType = ImageType.POSTER
				)
			}.forEach { row ->
				// Add row to adapter
				row.addToRowsAdapter(context, presenter, rowsAdapter)
			}
	}

	companion object {
		// Collections excluded from latest row based on app support and common sense
		private val EXCLUDED_COLLECTION_TYPES = arrayOf(
			CollectionType.Playlists,
			CollectionType.LiveTv,
			CollectionType.BoxSets,
			CollectionType.Books,
		)

		// Maximum amount of items loaded for a row
		private const val ITEM_LIMIT = 50
	}
}
