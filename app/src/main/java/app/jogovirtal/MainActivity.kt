package app.jogovirtal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import app.jogovirtal.domain.PetRules
import app.jogovirtal.domain.PetStore
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberOnGestureListener
import java.util.concurrent.atomic.AtomicReference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { PetExperience() }
        }
    }
}

@Composable
private fun PetExperience() {
    val context = LocalContext.current
    val store = remember(context) { PetStore(context.applicationContext) }
    var pet by remember(store) { mutableStateOf(store.load()) }
    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> cameraGranted = granted }

    LaunchedEffect(Unit) {
        if (!cameraGranted) cameraRequest.launch(Manifest.permission.CAMERA)
    }

    val careForPet = {
        val next = PetRules.pet(pet)
        store.save(next)
        pet = next
    }
    val restPet = {
        val next = PetRules.rest(pet)
        store.save(next)
        pet = next
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraGranted) {
            var anchor by remember { mutableStateOf<Anchor?>(null) }
            // Do not trigger Compose recomposition on every 30-60 fps AR frame.
            val latestFrame = remember { AtomicReference<Frame?>(null) }

            ARSceneView(
                modifier = Modifier.fillMaxSize(),
                planeRenderer = true,
                onSessionUpdated = { _, frame -> latestFrame.set(frame) },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { event, node ->
                        if (node != null) {
                            careForPet()
                        } else {
                            latestFrame.get()?.hitTest(event)
                                ?.firstOrNull {
                                    val plane = it.trackable as? Plane
                                    plane != null &&
                                        plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                        plane.isPoseInPolygon(it.hitPose)
                                }
                                ?.createAnchorOrNull()
                                ?.let { anchor = it }
                        }
                    }
                )
            ) {
                val model = rememberModelInstance(modelLoader, "models/pet.glb")
                anchor?.let { placed ->
                    AnchorNode(anchor = placed) {
                        model?.let {
                            ModelNode(modelInstance = it, scaleToUnits = 0.35f)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("A câmera é usada somente no aparelho para criar o mundo do pet.")
                Button(onClick = { cameraRequest.launch(Manifest.permission.CAMERA) }) {
                    Text("Permitir câmera")
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.62f)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("JogoVirtal", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text(
                "Toque no chão para posicionar o bichinho",
                color = Color.White
            )
            Text(
                "Humor: " + pet.mood.name + "  •  Energia: " + pet.energy +
                    "  •  Carinho: " + pet.affection,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = careForPet) { Text("Fazer carinho") }
            Button(onClick = restPet) { Text("Descansar") }
        }
    }
}
