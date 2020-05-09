package com.redapparat.layoutverifier

import com.redapparat.layoutverifier.extractor.DefaultFeatures

internal object Schemas {

    const val latestVersion = Version2.code

    fun newlyAddedFeatures(fromInclusive: Int): Set<String> {
        val result = mutableSetOf<String>()

        if (fromInclusive < Version2.code) {
            result += Version2.addedFeatures
        }

        return result
    }

    object Version2 {
        const val code = 2

        val addedFeatures = setOf(
            DefaultFeatures.TEXT_COLOR
        )

    }

}