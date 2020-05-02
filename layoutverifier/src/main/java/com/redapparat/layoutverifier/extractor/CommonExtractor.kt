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
            DefaultFeatures.TYPE to view.javaClass.name
        )
    }
}