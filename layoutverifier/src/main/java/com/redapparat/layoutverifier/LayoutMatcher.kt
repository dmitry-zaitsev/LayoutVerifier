package com.redapparat.layoutverifier

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.assertEquals
import java.io.File
import java.io.IOException
import java.io.Serializable

class LayoutMatcher internal constructor(
    private val view: View,
    private val configuration: LayoutVerifier.Configuration
) {

    private var widthPx = 600
    private var heightPx = 800

    fun screenSizePx(widthPx: Int, heightPx: Int): LayoutMatcher {
        this.widthPx = widthPx
        this.heightPx = heightPx

        return this
    }

    fun screenSize(widthDp: Int, heightDp: Int): LayoutMatcher {
        return screenSizePx(
            dpToPx(widthDp.toFloat()).toInt(),
            dpToPx(heightDp.toFloat()).toInt()
        )
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            view.resources.displayMetrics
        )
    }

    fun match(testName: String) {
        prepareView(view)

        val features = extractFeaturesRecursively(view)

        val testSnapshotFile = snapshotFileForTest(testName)
        if (testSnapshotFile.exists()) {
            val snapshot = configuration.serializer.deserializeFromStream(
                testSnapshotFile.inputStream()
            )

            assertEquals(snapshot, features)
        } else {
            ensureSnapshotsDirectoryExists()

            configuration.serializer.serializeToStream(
                features,
                testSnapshotFile.outputStream()
            )
        }
    }

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
        val viewFeatures = configuration.featureExtractor.extractFeatures(view)

        if (view !is ViewGroup) {
            return viewFeatures
        }

        val childrenFeatures = arrayListOf<Map<String, Serializable>>()

        for (i in 0 until view.childCount) {
            childrenFeatures.add(
                extractFeaturesRecursively(view.getChildAt(i))
            )
        }

        return viewFeatures + hashMapOf("children" to childrenFeatures)
    }

}