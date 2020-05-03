package com.redapparat.layoutverifier

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.redapparat.layoutverifier.extractor.CompositeFeatureExtractor
import com.redapparat.layoutverifier.extractor.FeatureExtractor
import com.redapparat.layoutverifier.extractor.CommonExtractor
import com.redapparat.layoutverifier.extractor.content.TextViewExtractor
import com.redapparat.layoutverifier.serializer.GsonSerializer
import com.redapparat.layoutverifier.serializer.Serializer
import java.io.File

class LayoutVerifier private constructor(
    private val context: Context,
    private val configuration: Configuration
) {

    fun layout(@LayoutRes layoutId: Int): LayoutMatcher {
        val view = LayoutInflater.from(context).inflate(layoutId, null);

        return LayoutMatcher(view, configuration)
    }
    
    fun view(view: View): LayoutMatcher {
        return LayoutMatcher(view, configuration)
    }

    class Builder(private val context: Context) {

        private var featureExtractors: MutableList<FeatureExtractor> = arrayListOf()
        private var snapshotsDirectory = "layoutSnapshots"

        fun addFeatureExtractor(featureExtractor: FeatureExtractor): Builder {
            featureExtractors.add(featureExtractor)
            return this
        }

        fun snapshotsDirectory(path: String): Builder {
            snapshotsDirectory = path
            return this
        }

        fun build(): LayoutVerifier {
            return LayoutVerifier(
                context = context,
                configuration = Configuration(
                    featureExtractor = CompositeFeatureExtractor(
                        listOf(
                            CommonExtractor(),
                            TextViewExtractor()
                        ) + featureExtractors
                    ),
                    snapshotsDirectory = File(snapshotsDirectory),
                    serializer = GsonSerializer()
                )
            )
        }

    }

    internal class Configuration(
        val featureExtractor: FeatureExtractor,
        val snapshotsDirectory: File,
        val serializer: Serializer
    )

}