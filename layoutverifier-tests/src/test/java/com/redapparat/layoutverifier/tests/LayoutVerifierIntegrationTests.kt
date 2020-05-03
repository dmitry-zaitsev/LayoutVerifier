package com.redapparat.layoutverifier.tests

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.redapparat.layoutverifier.LayoutVerifier
import com.redapparat.layoutverifier.extractor.DefaultFeatures
import com.redapparat.layoutverifier.tests.view.SimpleAdapter
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.AssertionError

@RunWith(AndroidJUnit4::class)
class LayoutVerifierIntegrationTests {

    @Test
    fun `Case 1 - Flat layout - Success`() {
        defaultLayoutVerifier()
            .layout(R.layout.case_001)
            .match("flat_layout")
    }

    @Test
    fun `Case 2 - Flat layout - Failure, layout structure is different`() {
        assertException(AssertionError::class.java) {
            defaultLayoutVerifier()
                .layout(R.layout.case_002)
                .match("flat_layout")
        }
    }

    @Test
    fun `Case 3 - Flat layout - Failure, content is different`() {
        assertException(AssertionError::class.java) {
            defaultLayoutVerifier()
                .layout(R.layout.case_003)
                .match("flat_layout")
        }
    }

    @Test
    fun `Case 4 - Flat layout - Success, different layout type but same output`() {
        defaultLayoutVerifier()
            .layout(R.layout.case_004)
            .match("flat_layout")
    }

    @Test
    fun `Case 5 - Flat layout - Failure, different layout with strict type matching`() {
        assertException(AssertionError::class.java) {
            defaultLayoutVerifier()
                .layout(R.layout.case_005)
                .matchType(true)
                .match("flat_layout")
        }
    }

    @Test
    fun `Case 6 - Flat layout - Success, only match given views`() {
        defaultLayoutVerifier()
            .layout(R.layout.case_006)
            .views(R.id.textView_b)
            .match("flat_layout__view_subset")
    }

    @Test
    fun `Case 7 - Flat layout - Success, ignore text`() {
        defaultLayoutVerifier()
            .layout(R.layout.case_007)
            .excludeFeatures(setOf(DefaultFeatures.TEXT))
            .match("flat_layout")
    }

    @Test
    fun `Case 8 - Flat layout - Small screen`() {
        defaultLayoutVerifier()
            .layout(R.layout.case_008)
            .screenSize(100, 100)
            .match("flat_layout__small_screen")
    }

    @Test
    fun `Case 9 - Nested layout - Success`() {
        defaultLayoutVerifier()
            .layout(R.layout.case_009)
            .match("nested_layout")
    }

    @Test
    fun `Case 10 - Recycler view - Success, content fits on the screen`() {
        val recyclerView = RecyclerView(getApplicationContext())
        recyclerView.layoutManager = LinearLayoutManager(getApplicationContext())
        recyclerView.adapter = SimpleAdapter(3)

        defaultLayoutVerifier()
            .view(recyclerView)
            .match("recycler_view__3_elements")
    }

    @Test
    fun `Case 11 - Recycler view - Success, content does not fit on the screen`() {
        val recyclerView = RecyclerView(getApplicationContext())
        recyclerView.layoutManager = LinearLayoutManager(getApplicationContext())
        recyclerView.adapter = SimpleAdapter(50)

        defaultLayoutVerifier()
            .view(recyclerView)
            .screenSize(240, 320)
            .match("recycler_view__50_elements")
    }

    private fun defaultLayoutVerifier() = LayoutVerifier.Builder(getApplicationContext())
        .build()

    private fun assertException(type: Class<*>, operation: () -> Any) {
        try {
            operation()
            fail("Expected exception of type $type. No exception received.")
        } catch (e: Throwable) {
            if (!type.isAssignableFrom(e.javaClass)) {
                fail("Expected exception of type $type. Received: $e")
            }
        }
    }

}