package com.redapparat.layoutverifier.extractor

import android.view.View
import java.io.Serializable

interface FeatureExtractor {

    fun extractFeatures(view: View): Map<String, Serializable>

}