package com.mad.derivefromhand.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.mad.derivefromhand.domain.Classifier

class ImageAnalyzer(
    private val classifier: Classifier,
    private val onResult: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter == 60) {
            val bitmap = image
                .toBitmap()
                .centerCrop(300, 300)

            val result = classifier.classify(bitmap)
            onResult(result)
            frameSkipCounter = 0
        }
        frameSkipCounter++

        image.close()
    }
}