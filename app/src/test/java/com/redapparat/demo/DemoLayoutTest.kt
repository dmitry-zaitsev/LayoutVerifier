package com.redapparat.demo

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.redapparat.layoutverifier.LayoutVerifier
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoLayoutTest {

    lateinit var layoutVerifier: LayoutVerifier

    @Before
    fun setUp() {
        layoutVerifier = LayoutVerifier.Builder(getApplicationContext())
            .build()
    }

    @Test
    fun `Flat layout`() {
        layoutVerifier.layout(R.layout.e1_flat_layout)
            .match("e1_flat_layout")
    }

    @Test
    fun `Flat layout on a small screen`() {
        layoutVerifier.layout(R.layout.e1_flat_layout)
            .withScreenSize(240, 320)
            .match("e1_flat_layout_240x320")
    }

    @Test
    fun `Nested layout`() {
        layoutVerifier.layout(R.layout.e2_nested_layout)
            .match("e2_nested_layout")
    }

}