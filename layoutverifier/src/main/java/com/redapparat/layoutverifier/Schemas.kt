package com.redapparat.layoutverifier

internal object Schemas {

    const val latestVersion = Version2.code

    fun newlyAddedFeatures(fromInclusive: Int): Set<String> {
        return emptySet()
    }

    object Version2 {
        const val code = 2
    }

}