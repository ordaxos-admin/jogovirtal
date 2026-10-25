package app.jogovirtal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberOnGestureListener
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.delay

private enum class ViewMode { CHECKING, AR, MODEL_3D }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { PetExperience() } }
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

    // The 3D pet is a first-class fallback: no ARCore installation, camera, or network required.
    var viewMode by remember { mutableStateOf(ViewMode.CHECKING) }
    var arAvailability by remember { mutableStateOf(ArCoreApk.Availability.UNKNOWN_CHECKING) }
    var arError by remember { mutableStateOf<String?>(null) }
    var checkVersion by remember { mutableIntStateOf(0) }

    LaunchedEffect(checkVersion) {
        viewMode = ViewMode.CHECKING
        arError = null
        try {
            var availability = ArCoreApk.getInstance().checkAvailability(context)
            var attempts = 0
            while (availability == ArCoreApk.Availability.UNKNOWN_CHECKING && attempts++ < 16) {
                delay(300)
                availability = ArCoreApk.getInstance().checkAvailability(context)
            }
            arAvailability = availability
            if (viewMode == ViewMode.CHECKING) {
                when (availability) {
                    ArCoreApk.Availability.SUPPORTED_INSTALLED -> viewMode = ViewMode.AR
                    ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD,
                    ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                        arError = "O serviço de realidade aumentada precisa ser instalado ou atualizado."
                        viewMode = ViewMode.MODEL_3D
                    }
                    ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                        arError = "Este aparelho não tem suporte ao ARCore. O bichinho funciona em 3D."
                        viewMode = ViewMode.MODEL_3D
                    }
                    else -> {
                        arError = "Não foi possível verificar o ARCore. Use o modo 3D ou tente novamente."
                        viewMode = ViewMode.MODEL_3D
                    }
                }
            }
        } catch (exception: Exception) {
            Log.w("JogoVirtal", "ARCore compatibility check failed", exception)
            arError = "Verificação do ARCore falhou: ${exception.javaClass.simpleName}"
            viewMode = ViewMode.MODEL_3D
        }
    }

    LaunchedEffect(viewMode) {
        if (viewMode == ViewMode.AR) {
            cameraGranted =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
            if (!cameraGranted) cameraRequest.launch(Manifest.permission.CAMERA)
        }
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
        when (viewMode) {
            ViewMode.CHECKING -> {
                Text(
                    "Verificando suporte à realidade aumentada...",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            }
            ViewMode.MODEL_3D -> {
                SceneView(modifier = Modifier.fillMaxSize()) {
                    rememberModelInstance(modelLoader, "models/pet.glb")?.let { instance ->
                        ModelNode(modelInstance = instance, scaleToUnits = 0.35f, autoAnimate = true)
                    }
                }
            }
            ViewMode.AR -> {
                if (cameraGranted) {
                    var anchor by remember { mutableStateOf<Anchor?>(null) }
                    DisposableEffect(anchor) {
                        val ownedAnchor = anchor
                        onDispose { ownedAnchor?.detach() }
                    }
                    // ARCore gives a fresh Frame frequently. Avoid 30–60 recompositions/sec.
                    val latestFrame = remember { AtomicReference<Frame?>(null) }
                    ARSceneView(
                        modifier = Modifier.fillMaxSize(),
                        planeRenderer = true,
                        sessionConfiguration = { session, config ->
                            config.depthMode =
                                if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
                                    Config.DepthMode.AUTOMATIC
                                else Config.DepthMode.DISABLED
                            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                        },
                        onSessionFailed = { exception ->
                            Log.e("JogoVirtal", "AR session failed", exception)
                            arError = "Falha ao iniciar AR: ${exception.javaClass.simpleName}"
                            viewMode = ViewMode.MODEL_3D
                        },
                        onARCoreAvailability = { availability ->
                            if (availability != null) {
                                arError = "O serviço ARCore não está pronto neste aparelho."
                                viewMode = ViewMode.MODEL_3D
                            }
                        },
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
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("A câmera precisa de autorização para usar a realidade aumentada.")
                        Button(onClick = {
                            cameraRequest.launch(Manifest.permission.CAMERA)
                        }) { Text("Permitir câmera") }
                        OutlinedButton(onClick = { viewMode = ViewMode.MODEL_3D }) {
                            Text("Usar em 3D sem câmera")
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.70f)).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("JogoVirtal", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text(
                when (viewMode) {
                    ViewMode.AR -> "Modo AR: toque numa superfície para posicionar o bichinho"
                    ViewMode.MODEL_3D -> "Modo 3D: bichinho virtual sem câmera"
                    ViewMode.CHECKING -> "Verificando o celular..."
                },
                color = Color.White
            )
            arError?.let { Text(it, color = Color.White) }
            if (viewMode == ViewMode.MODEL_3D && arError != null) {
                Text("Diagnóstico ARCore: ${arAvailability.name}", color = Color.White)
            }
            Text(
                "Humor: ${pet.mood.name} • Energia: ${pet.energy} • Carinho: ${pet.affection}",
                color = Color.White
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = careForPet) { Text("Fazer carinho") }
            Button(onClick = restPet) { Text("Descansar") }
            when (viewMode) {
                ViewMode.AR -> OutlinedButton(onClick = { viewMode = ViewMode.MODEL_3D }) {
                    Text("Modo 3D")
                }
                ViewMode.MODEL_3D -> {
                    if (arAvailability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED ||
                        arAvailability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD
                    ) {
                        OutlinedButton(onClick = {
                            val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.ar.core")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }) { Text("Instalar/atualizar serviços de RA") }
                    }
                    OutlinedButton(onClick = { checkVersion++ }) { Text("Tentar AR novamente") }
                }
                ViewMode.CHECKING -> OutlinedButton(
                    onClick = { viewMode = ViewMode.MODEL_3D }
                ) { Text("Continuar sem AR") }
            }
        }
    }
}
