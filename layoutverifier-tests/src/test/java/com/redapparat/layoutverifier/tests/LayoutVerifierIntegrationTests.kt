package com.redapparat.layoutverifier.tests

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.redapparat.layoutverifier.LayoutVerifier
import com.redapparat.layoutverifier.Schemas
import com.redapparat.layoutverifier.extractor.DefaultFeatures
import com.redapparat.layoutverifier.serializer.GsonSerializer
import com.redapparat.layoutverifier.tests.view.SimpleAdapter
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
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

    @Test
    fun `Case 12 - Schema version is the same - Do not rewrite snapshot file`() {
        // Cleanup
        val snapshotFile = File(
            LayoutVerifier.DEFAULT_SNAPSHOTS_PATH,
            "schema_version_idempotency.json"
        )

        // Run test once
        defaultLayoutVerifier()
            .layout(R.layout.case_001)
            .match("schema_version_idempotency")

        // Ensure file exists
        assertTrue("Snapshot file exists", snapshotFile.exists())

        // Read snapshot for the first run
        val snapshotOnFirstRun = GsonSerializer().deserializeFromStream(snapshotFile.inputStream())

        // Run test second time which will result in a different snapshot file
        defaultLayoutVerifier()
            .layout(R.layout.case_007)
            .excludeFeatures(setOf(DefaultFeatures.TEXT))
            .match("schema_version_idempotency")

        // Read snapshot for the second run
        val snapshotOnSecondRun = GsonSerializer().deserializeFromStream(snapshotFile.inputStream())

        // Two runs should given an identical snapshot since the second run would not overwrite the
        // file
        assertEquals(
            snapshotOnFirstRun,
            snapshotOnSecondRun
        )
    }

    @Test
    fun `Case 13 - Schema version changed - Success, layout is valid in old schema`() {
        // Run test on old schema
        LayoutVerifier.Builder(getApplicationContext())
            .schemaVersion(1)
            .build()
            .layout(R.layout.case_001)
            .match("schema_update_success")

        // Run test on new schema. Should succeed.
        LayoutVerifier.Builder(getApplicationContext())
            .schemaVersion(Schemas.latestVersion)
            .build()
            .layout(R.layout.case_001)
            .match("schema_update_success")
    }

    @Test
    fun `Case 13 - Schema version changed - Failure, layout is not valid in old schema`() {
        // Run test on old schema
        LayoutVerifier.Builder(getApplicationContext())
            .schemaVersion(1)
            .build()
            .layout(R.layout.case_001)
            .match("schema_update_failure")

        // Run test on new schema. Should fail.
        assertException(AssertionError::class.java) {
            LayoutVerifier.Builder(getApplicationContext())
                .schemaVersion(Schemas.latestVersion)
                .build()
                .layout(R.layout.case_002)
                .match("schema_update_failure")
        }
    }

    @After
    fun deleteTemporarySnapshotFiles() {
        val testNames = listOf(
            "schema_version_idempotency",
            "schema_update_success",
            "schema_update_failure"
        )

        testNames
            .map { File(LayoutVerifier.DEFAULT_SNAPSHOTS_PATH, "$it.json") }
            .forEach {
                if (it.exists()) {
                    assertTrue(it.delete())
                }
            }
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