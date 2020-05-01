package com.redapparat.layoutverifier.extractor

import android.view.View
import java.io.Serializable

class CompositeFeatureExtractor(
    private val wrappedExtractors: List<FeatureExtractor>
) : FeatureExtractor {

    override fun extractFeatures(view: View): Map<String, Serializable> {
        val result = mutableMapOf<String, Serializable>()

        wrappedExtractors
            .map { it.extractFeatures(view) }
            .forEach {
                val intersection = result.keys.intersect(it.keys)
                if (intersection.isNotEmpty()) {
                    throw IllegalStateException("Feature ${intersection.first()} is already defined for $view")
                }

                result.putAll(it)
            }

        return result
    }
}