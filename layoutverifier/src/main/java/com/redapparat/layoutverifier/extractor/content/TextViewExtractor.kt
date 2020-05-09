package com.redapparat.layoutverifier.extractor.content

import android.view.View
import android.widget.TextView
import com.redapparat.layoutverifier.extractor.DefaultFeatures
import com.redapparat.layoutverifier.extractor.FeatureExtractor
import java.io.Serializable

class TextViewExtractor : FeatureExtractor {
    override fun extractFeatures(view: View): Map<String, Serializable> {
        if (view !is TextView) {
            return emptyMap()
        }

        return mapOf(
            DefaultFeatures.TEXT to view.text.toString(),
            DefaultFeatures.TEXT_COLOR to "#${Integer.toHexString(view.currentTextColor)}",
            DefaultFeatures.TEXT_SIZE to view.textSize
        )
    }
}