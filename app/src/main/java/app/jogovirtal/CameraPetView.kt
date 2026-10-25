package app.jogovirtal

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.sceneview.SceneView
import io.github.sceneview.SurfaceType
import io.github.sceneview.rememberModelInstance

/**
 * Camera preview with an ordinary 3D overlay, NOT world-tracked AR.
 *
 * No ARCore dependency, depth estimation, anchors, recording or network calls.
 * The pet stays centred in the screen; device motion does not move it in world space.
 */
@Composable
internal fun CameraPetView(onFailure: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestFailure by rememberUpdatedState(onFailure)
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(context, lifecycleOwner, previewView) {
        val future = ProcessCameraProvider.getInstance(context)
        var disposed = false
        var provider: ProcessCameraProvider? = null
        val listener = Runnable {
            if (!disposed) {
                try {
                    val cameraProvider = future.get()
                    if (!disposed) {
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview
                        )
                        provider = cameraProvider
                    }
                } catch (error: Exception) {
                    Log.e("JogoVirtal", "CameraX preview unavailable", error)
                    latestFailure("Não foi possível abrir a câmera: ${error.javaClass.simpleName}")
                }
            }
        }
        future.addListener(listener, ContextCompat.getMainExecutor(context))
        onDispose {
            disposed = true
            provider?.unbindAll()
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        // The transparent TextureSurface composites the 3D pet over CameraX.
        SceneView(
            modifier = Modifier.fillMaxSize(),
            surfaceType = SurfaceType.TextureSurface,
            isOpaque = false,
            cameraManipulator = null
        ) {
            rememberModelInstance(modelLoader, "models/pet.glb")?.let {
                ModelNode(modelInstance = it, scaleToUnits = 0.95f, autoAnimate = true)
            }
        }
    }
}
