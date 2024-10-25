package com.mad.derivefromhand.presentation

import android.graphics.Bitmap

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("desiredWidth and desiredHeight must be smaller than the original image")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}