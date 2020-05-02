package com.redapparat.layoutverifier.extractor

import android.view.View
import java.io.Serializable

class CommonExtractor : FeatureExtractor {
    override fun extractFeatures(view: View): Map<String, Serializable> {
        return mapOf(
            DefaultFeatures.LEFT to view.left.toDouble(),
            DefaultFeatures.TOP to view.top.toDouble(),
            DefaultFeatures.RIGHT to view.right.toDouble(),
            DefaultFeatures.BOTTOM to view.bottom.toDouble(),
            DefaultFeatures.TYPE to view.javaClass.name,
            DefaultFeatures.ENABLED to view.isEnabled,
            DefaultFeatures.CLICKABLE to view.isClickable,
            DefaultFeatures.ID to displayableId(view)
        )
    }

    private fun displayableId(view: View): String {
        if (view.id == View.NO_ID) {
            return "No id"
        }

        return view.resources.getResourceName(view.id);
    }
}