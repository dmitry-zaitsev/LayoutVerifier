package com.redapparat.layoutverifier.extractor

import android.view.View
import java.io.Serializable

class LayoutPositionExtractor : FeatureExtractor {
    override fun extractFeatures(view: View): Map<String, Serializable> {
        return mapOf(
            "left" to view.left.toDouble(),
            "top" to view.top.toDouble(),
            "right" to view.right.toDouble(),
            "bottom" to view.bottom.toDouble()
        )
    }
}