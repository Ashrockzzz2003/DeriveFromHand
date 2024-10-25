package com.mad.derivefromhand.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.mad.derivefromhand.domain.Classifier
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.FloatBuffer

class TfLiteClassifier(
    private val context: Context
) : Classifier {
    private var interpreter: Interpreter? = null

    companion object {
        private const val TAG = "DigitClassificationHelper"
    }

    init {
        initHelper()
    }

    private fun initHelper() {
        interpreter = try {
            val litertBuffer = FileUtil.loadMappedFile(context, "model.tflite")
            Log.i(TAG, "Done creating TFLite buffer from asset")
            Interpreter(litertBuffer, Interpreter.Options())
        } catch (e: Exception) {
            Log.e(TAG, "Initializing TensorFlow Lite has failed with error: ${e.message}")
            return
        }
    }

    override fun classify(bitmap: Bitmap) : String {
            if (interpreter == null) initHelper()

            // Get the input tensor shape from the interpreter.
            val (_, h, w, _) = interpreter?.getInputTensor(0)?.shape() ?: return "Something went wrong"

            // Build an image processor for pre-processing the input image.
            val imageProcessor =
                ImageProcessor.Builder().add(ResizeOp(h, w, ResizeOp.ResizeMethod.BILINEAR))
                    .add(NormalizeOp(0f, 1f)).build()

            // Preprocess the image and convert it into a TensorImage for classification.
            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
            val output = classifyWithTFLite(tensorImage)

        return if (output[0] == 0f) {
            "It's a Male hand. Surprised?"
        }  else {
            "It's a Female hand. Surprised?"
        }
    }

    private fun classifyWithTFLite(tensorImage: TensorImage): FloatArray {
        val outputShape = interpreter!!.getOutputTensor(0).shape()
        val outputBuffer = FloatBuffer.allocate(outputShape[1])
        val inputBuffer = TensorBuffer.createFrom(tensorImage.tensorBuffer, DataType.FLOAT32).buffer

        inputBuffer.rewind()
        outputBuffer.rewind()
        interpreter?.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()
        val output = FloatArray(outputBuffer.capacity())
        outputBuffer.get(output)
        return output
    }

}