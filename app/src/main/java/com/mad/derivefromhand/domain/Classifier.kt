package com.mad.derivefromhand.domain

import android.graphics.Bitmap

interface Classifier {
    fun classify(bitmap: Bitmap): String
}