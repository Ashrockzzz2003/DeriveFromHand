package com.mad.derivefromhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mad.derivefromhand.data.TfLiteClassifier
import com.mad.derivefromhand.presentation.CameraPreview
import com.mad.derivefromhand.presentation.ImageAnalyzer
import com.mad.derivefromhand.ui.theme.DeriveFromHandTheme

class MainActivity : ComponentActivity() {
    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.CAMERA
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                0
            )
        }
        setContent {
            DeriveFromHandTheme {
                var (classification, setClassification) = remember {
                    mutableStateOf("")
                }

                val analyzer = remember {
                    ImageAnalyzer(
                        classifier = TfLiteClassifier(applicationContext),
                        onResult = { setClassification(it) }
                    )
                }

                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(
                            ContextCompat.getMainExecutor(applicationContext),
                            analyzer
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CameraPreview(
                        controller,
                        modifier = Modifier.fillMaxSize(),
                    )

                    Text(
                        text = classification,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(48.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}