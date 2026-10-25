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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
import io.github.sceneview.SurfaceType
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberOnGestureListener
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.delay

private enum class ViewMode { CHECKING, AR, MODEL_3D, CAMERA_PET }

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

    var viewMode by remember { mutableStateOf(ViewMode.CHECKING) }
    var arAvailability by remember { mutableStateOf(ArCoreApk.Availability.UNKNOWN_CHECKING) }
    var arError by remember { mutableStateOf<String?>(null) }
    var showDiagnostics by remember { mutableStateOf(false) }
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
                        arError = "Instale ou atualize os Serviços do Google Play para RA."
                        viewMode = ViewMode.MODEL_3D
                    }
                    ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                        arError = "Este celular não suporta RA espacial. O pet continua disponível."
                        viewMode = ViewMode.MODEL_3D
                    }
                    else -> {
                        arError = "Não foi possível verificar o ARCore."
                        viewMode = ViewMode.MODEL_3D
                    }
                }
            }
        } catch (error: Exception) {
            Log.w("JogoVirtal", "ARCore compatibility check failed", error)
            arError = "Verificação de RA indisponível: ${error.javaClass.simpleName}"
            viewMode = ViewMode.MODEL_3D
        }
    }

    LaunchedEffect(viewMode) {
        if (viewMode == ViewMode.AR || viewMode == ViewMode.CAMERA_PET) {
            cameraGranted =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
            if (!cameraGranted) cameraRequest.launch(Manifest.permission.CAMERA)
        }
    }

    val careForPet = {
        val updated = PetRules.pet(pet)
        store.save(updated)
        pet = updated
    }
    val restPet = {
        val updated = PetRules.rest(pet)
        store.save(updated)
        pet = updated
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF252D43), Color(0xFF505B78), Color(0xFF141827))
            )
        )
    ) {
        when (viewMode) {
            ViewMode.CHECKING -> Text(
                "Verificando a câmera e a realidade aumentada...",
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            ViewMode.MODEL_3D -> {
                SceneView(
                    modifier = Modifier.fillMaxSize(),
                    surfaceType = SurfaceType.TextureSurface,
                    isOpaque = false
                ) {
                    rememberModelInstance(modelLoader, "models/pet.glb")?.let {
                        ModelNode(modelInstance = it, scaleToUnits = 0.95f, autoAnimate = true)
                    }
                }
            }

            ViewMode.CAMERA_PET -> {
                if (cameraGranted) {
                    CameraPetView { message ->
                        arError = message
                        viewMode = ViewMode.MODEL_3D
                    }
                } else {
                    CameraPermissionPanel(
                        onRequest = { cameraRequest.launch(Manifest.permission.CAMERA) },
                        onContinueWithoutCamera = { viewMode = ViewMode.MODEL_3D }
                    )
                }
            }

            ViewMode.AR -> {
                if (cameraGranted) {
                    var anchor by remember { mutableStateOf<Anchor?>(null) }
                    DisposableEffect(anchor) {
                        val ownedAnchor = anchor
                        onDispose { ownedAnchor?.detach() }
                    }
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
                        onSessionFailed = { error ->
                            Log.e("JogoVirtal", "AR session failed", error)
                            arError = "A realidade aumentada não iniciou: ${error.javaClass.simpleName}"
                            viewMode = ViewMode.MODEL_3D
                        },
                        onARCoreAvailability = { availability ->
                            if (availability != null) {
                                arError = "Serviços de realidade aumentada indisponíveis."
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
                                model?.let { ModelNode(modelInstance = it, scaleToUnits = 0.35f) }
                            }
                        }
                    }
                } else {
                    CameraPermissionPanel(
                        onRequest = { cameraRequest.launch(Manifest.permission.CAMERA) },
                        onContinueWithoutCamera = { viewMode = ViewMode.MODEL_3D }
                    )
                }
            }
        }

        // Respect system status bar and keep this panel short to preserve scene space.
        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                .statusBarsPadding().background(Color(0xDE161B2B))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text("JogoVirtal", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text(
                when (viewMode) {
                    ViewMode.AR -> "Toque no chão ou mesa para posicionar o bichinho"
                    ViewMode.MODEL_3D -> "Seu bichinho em 3D"
                    ViewMode.CAMERA_PET -> "Câmera + pet na tela (sem rastreamento espacial)"
                    ViewMode.CHECKING -> "Preparando seu bichinho..."
                },
                color = Color(0xFFDCE6F5),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Humor: ${pet.mood.name}  •  Energia: ${pet.energy}  •  Carinho: ${pet.affection}",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            if (showDiagnostics) {
                arError?.let {
                    Text(it, color = Color(0xFFFFD9A0), style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "ARCore: ${arAvailability.name}",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .navigationBarsPadding().background(Color(0xD71C2436))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = careForPet, modifier = Modifier.weight(1f)) {
                    Text("Fazer carinho")
                }
                Button(onClick = restPet, modifier = Modifier.weight(1f)) {
                    Text("Descansar")
                }
            }

            when (viewMode) {
                ViewMode.MODEL_3D -> {
                    Button(
                        onClick = { viewMode = ViewMode.CAMERA_PET },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver bichinho pela câmera")
                    }
                    if (arAvailability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED ||
                        arAvailability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD
                    ) {
                        TextButton(onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.ar.core")
                                )
                            )
                        }) { Text("Instalar serviços de RA", color = Color.White) }
                    }
                }
                ViewMode.CAMERA_PET -> OutlinedButton(
                    onClick = { viewMode = ViewMode.MODEL_3D },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Voltar ao modo 3D", color = Color.White) }
                ViewMode.AR -> OutlinedButton(
                    onClick = { viewMode = ViewMode.MODEL_3D },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Modo 3D", color = Color.White) }
                ViewMode.CHECKING -> OutlinedButton(
                    onClick = { viewMode = ViewMode.MODEL_3D },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Abrir sem AR", color = Color.White) }
            }

            TextButton(onClick = { showDiagnostics = !showDiagnostics }) {
                Text(if (showDiagnostics) "Ocultar diagnóstico" else "Detalhes da câmera e RA",
                    color = Color(0xFFCEDAF0),
                    style = MaterialTheme.typography.bodySmall)
            }
            if (showDiagnostics && viewMode == ViewMode.MODEL_3D &&
                arAvailability != ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE
            ) {
                TextButton(onClick = { checkVersion++ }) {
                    Text("Verificar RA novamente", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CameraPermissionPanel(
    onRequest: () -> Unit,
    onContinueWithoutCamera: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Para ver o bichinho pela câmera, permita o acesso à câmera.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRequest) { Text("Permitir câmera") }
        OutlinedButton(onClick = onContinueWithoutCamera) {
            Text("Continuar sem câmera", color = Color.White)
        }
    }
}
