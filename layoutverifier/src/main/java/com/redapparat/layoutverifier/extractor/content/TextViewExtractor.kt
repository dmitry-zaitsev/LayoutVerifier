package com.redapparat.layoutverifier.extractor.content

import android.view.View
import android.widget.TextView
import com.redapparat.layoutverifier.extractor.FeatureExtractor
import java.io.Serializable

class TextViewExtractor : FeatureExtractor {
    override fun extractFeatures(view: View): Map<String, Serializable> {
        if (view !is TextView) {
            return emptyMap()
        }

        return mapOf(
            "text" to view.text.toString()
        )
    }
}