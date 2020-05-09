package com.redapparat.layoutverifier

import com.redapparat.layoutverifier.extractor.DefaultFeatures

object Schemas {

    const val latestVersion = Version3.code

    fun newlyAddedFeatures(fromInclusive: Int): Set<String> {
        val result = mutableSetOf<String>()

        if (fromInclusive < Version2.code) {
            result += Version2.addedFeatures
        }

        if (fromInclusive < Version3.code) {
            result += Version3.addedFeatures
        }

        return result
    }

    object Version2 {
        const val code = 2

        val addedFeatures = setOf(
            DefaultFeatures.TEXT_COLOR
        )
    }

    object Version3 {
        const val code = 3

        val addedFeatures = setOf(
            DefaultFeatures.PADDING_START,
            DefaultFeatures.PADDING_TOP,
            DefaultFeatures.PADDING_END,
            DefaultFeatures.PADDING_BOTTOM,
            DefaultFeatures.VISIBILITY,
            DefaultFeatures.TEXT_SIZE,
            DefaultFeatures.BACKGROUND
        )
    }

}