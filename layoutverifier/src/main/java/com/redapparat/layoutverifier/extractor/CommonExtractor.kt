package com.redapparat.layoutverifier.extractor

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.ViewGroup
import java.io.Serializable

class CommonExtractor : FeatureExtractor {
    override fun extractFeatures(view: View): Map<String, Serializable> {
        return baseFeatures(view) + leafNodeFeatures(view)
    }

    private fun baseFeatures(view: View): Map<String, Serializable> {
        return mapOf(
            DefaultFeatures.LEFT to view.left.toDouble(),
            DefaultFeatures.TOP to view.top.toDouble(),
            DefaultFeatures.RIGHT to view.right.toDouble(),
            DefaultFeatures.BOTTOM to view.bottom.toDouble(),
            DefaultFeatures.TYPE to view.javaClass.name,
            DefaultFeatures.ENABLED to view.isEnabled,
            DefaultFeatures.CLICKABLE to view.isClickable,
            DefaultFeatures.ID to displayableId(view),
            DefaultFeatures.VISIBILITY to visibility(view),
            DefaultFeatures.BACKGROUND to background(view)
        )
    }

    private fun background(view: View): Serializable {
        return view.background
            ?.let { drawableAsString(it) }
            ?: return "null"
    }

    private fun drawableAsString(drawable: Drawable): String {
        if (drawable is ColorDrawable) {
            return "#${Integer.toHexString(drawable.color)}"
        }

        if (drawable is StateListDrawable) {
            return drawableAsString(drawable.current)
        }

        return "Drawable"
    }

    private fun visibility(view: View): Serializable {
        return when (view.visibility) {
            View.VISIBLE -> "VISIBLE"
            View.INVISIBLE -> "INVISIBLE"
            View.GONE -> "GONE"
            else -> "UNKNOWN"
        }
    }

    private fun leafNodeFeatures(view: View): Map<String, Serializable> {
        if (view is ViewGroup) {
            return emptyMap()
        }

        return mapOf(
            DefaultFeatures.PADDING_START to view.paddingStart.toDouble(),
            DefaultFeatures.PADDING_TOP to view.paddingTop.toDouble(),
            DefaultFeatures.PADDING_END to view.paddingEnd.toDouble(),
            DefaultFeatures.PADDING_BOTTOM to view.paddingBottom.toDouble()
        )
    }

    private fun displayableId(view: View): String {
        if (view.id == View.NO_ID) {
            return "No id"
        }

        return view.resources.getResourceName(view.id);
    }
}