package com.redapparat.layoutverifier.extractor

import android.view.View
import java.io.Serializable

/**
 * Extracts set of features (aka attributes) from a view to be used for further matching.
 *
 * Extracted features must be JSON-serializable.
 */
interface FeatureExtractor {

    /**
     * @see [FeatureExtractor]
     */
    fun extractFeatures(view: View): Map<String, Serializable>

}