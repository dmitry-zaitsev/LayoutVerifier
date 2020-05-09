package com.redapparat.layoutverifier

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.redapparat.layoutverifier.extractor.DefaultFeatures
import org.junit.Assert.assertEquals
import java.io.File
import java.io.IOException
import java.io.Serializable

/**
 * Performs layout matching based for a given view.
 *
 * Provide test-specific configuration (i.e. [LayoutMatcher.screenSize]) and
 * call [LayoutMatcher.match].
 *
 * See [LayoutVerifier] for usage examples.
 */
class LayoutMatcher internal constructor(
    private val view: View,
    private val configuration: LayoutVerifier.Configuration
) {

    private var widthPx = 600
    private var heightPx = 800

    private var viewIds: Set<Int>? = null
    private val excludedFeatures = mutableSetOf(
        DefaultFeatures.TYPE,
        DefaultFeatures.ID
    )

    /**
     * Set device screen size in pixels.
     */
    fun screenSizePx(widthPx: Int, heightPx: Int): LayoutMatcher {
        this.widthPx = widthPx
        this.heightPx = heightPx

        return this
    }

    /**
     * Set device screen size in DP.
     */
    fun screenSize(widthDp: Int, heightDp: Int): LayoutMatcher {
        return screenSizePx(
            dpToPx(widthDp.toFloat()).toInt(),
            dpToPx(heightDp.toFloat()).toInt()
        )
    }

    /**
     * Only match following view IDs when running test and ignore the rest.
     */
    fun views(@IdRes vararg viewIds: Int): LayoutMatcher {
        this.viewIds = viewIds.toSet()

        return this
    }

    /**
     * Enforce strict type matching (i.e. replacing RelativeLayout with ConstraintLayout will
     * cause a test failure).
     *
     * `false` by default.
     *
     * **Note**: `matchType(false)` is equivalent to `excludeFeatures(setOf(DefaultFeatures.TYPE))`.
     */
    fun matchType(match: Boolean): LayoutMatcher {
        if (match) {
            excludedFeatures.remove(DefaultFeatures.TYPE)
        } else {
            excludedFeatures.add(DefaultFeatures.TYPE)
        }

        return this
    }

    /**
     * Exclude features from matching process.
     *
     * Features excluded by default:
     * - [DefaultFeatures.TYPE]
     * - [DefaultFeatures.ID]
     */
    fun excludeFeatures(toExclude: Iterable<String>): LayoutMatcher {
        excludedFeatures.addAll(toExclude)

        return this
    }

    /**
     * Performs matching. If no pre-recorded test results are available, succeeds the test and
     * writes result to a file to be used for future test runs.
     *
     * Test name must be unique within build module (i.e. gradle module).
     */
    fun match(testName: String) {
        prepareView(view)

        val features = extractFeaturesRecursively(view)

        val testSnapshotFile = snapshotFileForTest(testName)
        if (testSnapshotFile.exists()) {
            val snapshot = configuration.serializer.deserializeFromStream(
                testSnapshotFile.inputStream()
            )

            val snapshotVersionCode = (snapshot["schemaVersion"] as? Double)?.toInt() ?: 0
            val combinedExcludedFeatures = excludedFeatures.toMutableSet()

            if (snapshotVersionCode < configuration.schemaVersion) {
                combinedExcludedFeatures += Schemas.newlyAddedFeatures(snapshotVersionCode)
            }

            val snapshotFeatures = readFeaturesFromSnapshot(snapshot)

            val expected = snapshotFeatures.excludeFeaturesRecursively(combinedExcludedFeatures)
            val actual = features.excludeFeaturesRecursively(combinedExcludedFeatures)

            if (expected != actual) {
                assertEquals(
                    configuration.serializer.toPrettyJson(expected),
                    configuration.serializer.toPrettyJson(actual)
                )
            } else {
                if (snapshotVersionCode < configuration.schemaVersion) {
                    saveSnapshot(features, testSnapshotFile)
                }
            }
        } else {
            saveSnapshot(features, testSnapshotFile)
        }
    }

    private fun saveSnapshot(
        features: Map<String, Serializable>,
        testSnapshotFile: File
    ) {
        ensureSnapshotsDirectoryExists()

        val featuresPayload = mapOf(
            "schemaVersion" to configuration.schemaVersion,
            "features" to features
        )

        configuration.serializer.serializeToStream(
            featuresPayload,
            testSnapshotFile.outputStream()
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun readFeaturesFromSnapshot(snapshot: Map<String, *>) =
        snapshot["features"] as? Map<String, *> ?: snapshot

    private fun ensureSnapshotsDirectoryExists() {
        if (configuration.snapshotsDirectory.exists()) {
            return
        }

        if (configuration.snapshotsDirectory.mkdirs()) {
            return
        }

        throw IOException("Unable to create snapshots directory: ${configuration.snapshotsDirectory}")
    }

    private fun snapshotFileForTest(testName: String): File {
        return File(
            configuration.snapshotsDirectory,
            "$testName.json"
        )
    }

    private fun prepareView(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, widthPx, heightPx)
    }

    private fun extractFeaturesRecursively(view: View): Map<String, Serializable> {
        val viewFeatures = if (filterView(view)) {
            configuration.featureExtractor.extractFeatures(view)
        } else {
            emptyMap()
        }

        if (view !is ViewGroup) {
            return viewFeatures
        }

        val childrenFeatures = arrayListOf<Map<String, Serializable>>()

        for (i in 0 until view.childCount) {
            val childFeatures = extractFeaturesRecursively(view.getChildAt(i))

            if (childFeatures.isNotEmpty()) {
                childrenFeatures.add(childFeatures)
            }
        }

        return viewFeatures + hashMapOf(ATTR_CHILDREN to childrenFeatures)
    }

    private fun filterView(view: View): Boolean {
        return viewIds?.let { view.id in it } ?: true
    }

    private fun Map<String, *>.excludeFeaturesRecursively(excludedFeatures: Iterable<String>): Map<String, *> {
        val copiedMap = hashMapOf<String, Any?>()

        copiedMap.putAll(this - (excludedFeatures + ATTR_CHILDREN))

        get(ATTR_CHILDREN)
            ?.let { list ->
                @Suppress("UNCHECKED_CAST")
                copiedMap[ATTR_CHILDREN] = (list as Iterable<Map<String, *>>)
                    .map {
                        it.excludeFeaturesRecursively(excludedFeatures)
                    }
            }

        return copiedMap
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            view.resources.displayMetrics
        )
    }

    companion object {

        private const val ATTR_CHILDREN = "children"

    }

}
