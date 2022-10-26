package org.jellyfin.androidtv.util

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import me.carleslc.kotlin.extensions.standard.letOrElse

fun View.setPaddingHorizontal(hPadding: Int) = this.setPadding(hPadding, paddingTop, hPadding, paddingBottom)
fun View.setPaddingVertical(vPadding: Int) = this.setPadding(paddingLeft, vPadding, paddingRight, vPadding)
fun View.setPaddingTop(top: Int) = this.setPadding(paddingLeft, top, paddingRight, paddingBottom)
fun View.setPaddingBottom(bottom: Int) = this.setPadding(paddingLeft, paddingTop, paddingRight, bottom)

fun View.setMarginHorizontal(hMargin: Int) = apply {
    layoutParams = ((layoutParams as? ViewGroup.MarginLayoutParams) ?: ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)).apply {
        marginStart = hMargin
        marginEnd = hMargin
    }
}

fun View.setMarginVertical(vMargin: Int) = apply {
    layoutParams = ((layoutParams as? ViewGroup.MarginLayoutParams) ?: ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)).apply {
        topMargin = vMargin
        bottomMargin = vMargin
    }
}

fun View.setMarginTop(top: Int) = apply {
    layoutParams = ((layoutParams as? ViewGroup.MarginLayoutParams) ?: ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)).apply {
        topMargin = top
    }
}

fun View.setMarginBottom(bottom: Int) = apply {
    layoutParams = ((layoutParams as? ViewGroup.MarginLayoutParams) ?: ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)).apply {
        bottomMargin = bottom
    }
}

fun View.setHeight(height: Int) = apply {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams).letOrElse(ViewGroup.MarginLayoutParams(WRAP_CONTENT, height)) {
        it.height = height
        return@letOrElse it
    }
}

fun View.setWidth(width: Int) = apply {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams).letOrElse(ViewGroup.MarginLayoutParams(width, WRAP_CONTENT)) {
        it.width = width
        return@letOrElse it
    }
}

fun View.setSize(width: Int, height: Int) = apply {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams).letOrElse(ViewGroup.MarginLayoutParams(width, height)) {
        it.width = width
        it.height = height
        return@letOrElse it
    }
}

fun View.setHeightDP(heightDP: Int) = apply {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams).letOrElse(ViewGroup.MarginLayoutParams(WRAP_CONTENT, heightDP.dp(context))) {
        it.height = heightDP.dp(context)
        return@letOrElse it
    }
}

fun View.setSizeDP(sizeDP: Int) = apply {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams).letOrElse(ViewGroup.MarginLayoutParams(sizeDP.dp(context), sizeDP.dp(context))) {
        it.width = sizeDP.dp(context)
        it.height = sizeDP.dp(context)
        return@letOrElse it
    }
}