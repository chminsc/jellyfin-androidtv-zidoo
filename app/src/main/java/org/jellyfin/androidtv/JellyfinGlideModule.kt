package org.jellyfin.androidtv

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.carleslc.kotlinextensions.bytes.bytes
import me.carleslc.kotlinextensions.bytes.gibibytes
import me.carleslc.kotlinextensions.bytes.megabytes
import org.jellyfin.androidtv.util.Utils.getDeviceMemorySize
import timber.log.Timber


@GlideModule
class JellyfinGlideModule : AppGlideModule() {
	companion object {
		suspend fun clearDiskCache() {
			JellyfinApplication.appContext.let {
				Timber.w("Clearing Glide Disk Cache !")
				withContext(Dispatchers.IO) {
					Glide.get(it).clearDiskCache()
				}
			}
		}
	}

	override fun applyOptions(context: Context, builder: GlideBuilder): Unit = with(builder) {
		// Set default disk cache strategy
		setDefaultRequestOptions(
			RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
		)
//		val calculator = MemorySizeCalculator.Builder(context).setMemoryCacheScreens(10f).build()
//		Timber.w("Glide Memory: <%s> MB", calculator.memoryCacheSize.bytes.toMegaBytes)

		val memSize = getDeviceMemorySize(context)
		val memCacheSize = when {
			(memSize > 3.gibibytes.toBytes)  -> 256.megabytes.toBytes
			(memSize < 1.gibibytes.toBytes)  -> 64.megabytes.toBytes
			else -> 128.megabytes.toBytes
		}
		setMemoryCache(LruResourceCache(memCacheSize))
		val diskCacheSize = 512.megabytes.toBytes
		setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSize))

		Timber.w("DeviceMemSize <${memSize.bytes.toMegaBytes} MB> -> MemCacheSize<${memCacheSize.bytes.toMegaBytes} MB> : DiskCacheSize<${diskCacheSize.bytes.toMegaBytes} MB>")

		// Silence image load errors
		setLogLevel(Log.ERROR)
	}
}
