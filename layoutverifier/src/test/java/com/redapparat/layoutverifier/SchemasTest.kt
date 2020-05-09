package com.redapparat.layoutverifier

import org.junit.Assert.*
import org.junit.Test

class SchemasTest {

    @Test
    fun `Newly added features since version 0`() {
        val result = Schemas.newlyAddedFeatures(0)

        assertEquals(
            Schemas.Version2.addedFeatures + Schemas.Version3.addedFeatures,
            result
        )
    }

    @Test
    fun `Newly added features since version 1`() {
        val result = Schemas.newlyAddedFeatures(1)

        assertEquals(
            Schemas.Version2.addedFeatures + Schemas.Version3.addedFeatures,
            result
        )
    }

    @Test
    fun `Newly added features since version 2`() {
        val result = Schemas.newlyAddedFeatures(2)

        assertEquals(
            Schemas.Version3.addedFeatures,
            result
        )
    }

    @Test
    fun `Newly added features since version 3`() {
        val result = Schemas.newlyAddedFeatures(3)

        assertEquals(
            emptySet<String>(),
            result
        )
    }

}