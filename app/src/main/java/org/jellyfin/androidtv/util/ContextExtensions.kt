package org.jellyfin.androidtv.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.util.TypedValue.*
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.appcompat.content.res.AppCompatResources
import org.apfloat.Apfloat
import org.apfloat.ApfloatMath
import timber.log.Timber
import java.math.RoundingMode
import kotlin.math.roundToInt

/**
 * Get the activity hosting the current context
 */
tailrec fun Context.getActivity(): Activity? = when (this) {
	is Activity -> this
	else -> (this as? ContextWrapper)?.baseContext?.getActivity()
}

fun Context.getDrawableFromAttribute(attributeId: Int): Drawable? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_REFERENCE, TYPE_STRING -> AppCompatResources.getDrawable(this, typedValue.resourceId)
		else -> {
			Timber.e("Unable to retrieve Drawable from attribute")
			null
		}
	}
}

@ColorInt
fun Context.getColorFromAttribute(attributeId: Int): Int {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_REFERENCE, TYPE_STRING, TYPE_INT_COLOR_ARGB8, TYPE_INT_COLOR_RGB8 -> AppCompatResources.getColorStateList(this, typedValue.resourceId).defaultColor
		else -> {
			Timber.e("Unable to retrieve ColorInt from attribute")
			Color.TRANSPARENT
		}
	}
}

fun Context.getBoolFromAttribute(attributeId: Int): Boolean {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_INT_BOOLEAN -> typedValue.data > 0
		else -> {
			Timber.e("Unable to retrieve Int from attribute")
			false
		}
	}
}

fun Context.getIntFromAttribute(attributeId: Int): Int? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_INT_DEC -> typedValue.data
		else -> {
			Timber.e("Unable to retrieve Int from attribute")
			null
		}
	}
}

fun Context.getFloatFromAttribute(attributeId: Int): Float? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_FLOAT -> typedValue.float
		else -> {
			Timber.e("Unable to retrieve Fraction from attribute")
			null
		}
	}
}

fun Context.getFloatFromAttributeApfloat(attributeId: Int): Apfloat? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_FLOAT -> Apfloat(typedValue.float.toString())
		else -> {
			Timber.e("Unable to retrieve Fraction from attribute")
			null
		}
	}
}

fun Context.getFractionFromAttribute(attributeId: Int): Float? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_FRACTION -> typedValue.getFraction(1.0f,1.0f)
		TYPE_FLOAT -> typedValue.float
		else -> {
			Timber.e("Unable to retrieve Fraction from attribute")
			null
		}
	}
}

fun Context.getFractionFromAttribute(attributeId: Int, precision: Long): Float? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_FRACTION -> ApfloatMath.round(Apfloat(typedValue.getFraction(1.0f,1.0f)), precision, RoundingMode.HALF_EVEN).toFloat()
		TYPE_FLOAT -> ApfloatMath.round(Apfloat(typedValue.float.toString()), precision, RoundingMode.HALF_EVEN).toFloat()
		else -> {
			Timber.e("Unable to retrieve Fraction from attribute")
			null
		}
	}
}

fun Context.getFractionFromAttributeApfloat(attributeId: Int, precision: Long): Apfloat? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_FRACTION -> ApfloatMath.round(Apfloat(typedValue.getFraction(1.0f,1.0f)), precision, RoundingMode.HALF_EVEN)
		TYPE_FLOAT -> ApfloatMath.round(Apfloat(typedValue.float.toString()), precision, RoundingMode.HALF_EVEN)
		else -> {
			Timber.e("Unable to retrieve Fraction from attribute")
			null
		}
	}
}

fun Context.getDimensionFromAttribute(attributeId: Int, default: Int): Int {
	return this.getDimensionFromAttribute(attributeId)?.roundToInt() ?: default
}

fun Context.getDimensionFromAttribute(attributeId: Int, default: Float): Float {
	return this.getDimensionFromAttribute(attributeId) ?: default
}

fun Context.getDimensionFromAttribute(attributeId: Int): Float? {
	val typedValue = TypedValue().also { theme.resolveAttribute(attributeId, it, true) }
	return when (typedValue.type) {
		TYPE_DIMENSION -> typedValue.getDimension(resources.displayMetrics)
		else -> {
			Timber.e("Unable to retrieve Dimension from attribute")
			null
		}
	}
}

fun Context.getRawDimension(@DimenRes dimenResId: Int): Float {
	val value = TypedValue()
	resources.getValue(dimenResId, value, true)
	return complexToFloat(value.data)
}