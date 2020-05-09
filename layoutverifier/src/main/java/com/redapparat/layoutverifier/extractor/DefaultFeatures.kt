package com.redapparat.layoutverifier.extractor

import android.view.View

object DefaultFeatures {

    /**
     * [View.getLeft]
     */
    const val LEFT = "left"

    /**
     * [View.getTop]
     */
    const val TOP = "top"

    /**
     * [View.getRight]
     */
    const val RIGHT = "right"

    /**
     * [View.getBottom]
     */
    const val BOTTOM = "bottom"

    /**
     * Class name of the view.
     */
    const val TYPE = "type"

    /**
     * Text drawn on the TextView, EditText, Button, etc.
     */
    const val TEXT = "text"

    /**
     * [View.isEnabled]
     */
    const val ENABLED = "enabled"

    /**
     * [View.isClickable]
     */
    const val CLICKABLE = "clickable"

    /**
     * [View.getId]
     */
    const val ID = "id"

    /**
     * Current color of a text.
     */
    const val TEXT_COLOR = "textColor"

    /**
     * Size of a text (in px).
     */
    const val TEXT_SIZE = "textSize"

    /**
     * [View.getPaddingStart]
     */
    const val PADDING_START = "paddingStart"

    /**
     * [View.getPaddingTop]
     */
    const val PADDING_TOP = "paddingTop"

    /**
     * [View.getPaddingEnd]
     */
    const val PADDING_END = "paddingEnd"

    /**
     * [View.getPaddingBottom]
     */
    const val PADDING_BOTTOM = "paddingBottom"

    /**
     * [View.getVisibility]
     */
    const val VISIBILITY = "visibility"

    /**
     * Background of a view (either color hex or `Drawable` as a literal)
     */
    const val BACKGROUND = "background"

}