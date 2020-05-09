package com.redapparat.layoutverifier

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.redapparat.layoutverifier.extractor.CompositeFeatureExtractor
import com.redapparat.layoutverifier.extractor.FeatureExtractor
import com.redapparat.layoutverifier.extractor.CommonExtractor
import com.redapparat.layoutverifier.extractor.DefaultFeatures
import com.redapparat.layoutverifier.extractor.content.TextViewExtractor
import com.redapparat.layoutverifier.serializer.GsonSerializer
import com.redapparat.layoutverifier.serializer.Serializer
import java.io.File

/**
 * Captures a snapshot of a layout structure and compares it to a pre-recorded structure saved
 * on the disk. If no pre-recorded is available, creates a new record and assumes it to be a source
 * of truth.
 *
 * ## Usage
 *
 * Create new instance by using [LayoutVerifier.Builder]. Specify layout and the name of the test
 * case:
 *
 * ```
 * layoutVerifier
 *     .layout(R.layout.myLayout)
 *     .match("unique_test_case_name")
 * ```
 *
 * **Note**: test case name must be unique per module (i.e. gradle module).
 *
 * ## Matching custom attributes
 * By default, the following layout features (aka attributes) are compared: [DefaultFeatures].
 *
 * New features can be added by providing custom implementations of [FeatureExtractor].
 */
class LayoutVerifier private constructor(
    private val context: Context,
    private val configuration: Configuration
) {

    /**
     * Starts matching for the given layout resource.
     *
     * Configure matching parameters and call [LayoutMatcher.match] to perform matching.
     */
    fun layout(@LayoutRes layoutId: Int): LayoutMatcher {
        val view = LayoutInflater.from(context).inflate(layoutId, null);

        return LayoutMatcher(view, configuration)
    }

    /**
     * Starts matching for the given view.
     *
     * Configure matching parameters and call [LayoutMatcher.match] to perform matching.
     */
    fun view(view: View): LayoutMatcher {
        return LayoutMatcher(view, configuration)
    }

    /**
     * Builds instances of [LayoutVerifier]
     */
    class Builder(private val context: Context) {

        private var featureExtractors: MutableList<FeatureExtractor> = arrayListOf()
        private var snapshotsDirectory = "layoutSnapshots"
        private var schemaVersion = Schemas.latestVersion

        /**
         * Registers additional custom feature (aka attribute) extractor to be used during matching.
         */
        fun addFeatureExtractor(featureExtractor: FeatureExtractor): Builder {
            featureExtractors.add(featureExtractor)
            return this
        }

        /**
         * Directory to store pre-recorded results in. Relative to the test execution directory.
         */
        fun snapshotsDirectory(path: String): Builder {
            snapshotsDirectory = path
            return this
        }

        internal fun schemaVersion(version: Int) {
            schemaVersion = version
        }

        /**
         * Builds a new instance.
         */
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
                    serializer = GsonSerializer(),
                    schemaVersion = schemaVersion
                )
            )
        }

    }

    internal class Configuration(
        val featureExtractor: FeatureExtractor,
        val snapshotsDirectory: File,
        val serializer: Serializer,
        val schemaVersion: Int
    )

}