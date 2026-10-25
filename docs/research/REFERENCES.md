# Referências da pesquisa e licenças

Atualizado em 2026-10-25. **Link verificado como referência**, não significa que todo componente esteja autorizado para redistribuição. Auditar licença de cada versão, asset, voz e modelo antes de embutir.

## Referências comerciais e pesquisa
- [Niantic Peridot](https://nianticlabs.com/) — conceito de pet que interage com o espaço real; comercial, não copiar código/assets.
- [Pokémon GO](https://pokemongolive.com/) — AR+ e ancoragem em superfícies.
- [My Tamagotchi Forever](https://www.bandainamcoent.com/) — pet e modo AR de esconder/brincar.
- [Niantic Spatial](https://www.nianticspatial.com/) — plataforma atual de percepção espacial; termos comerciais específicos.
- [CHI 2021: ACM Digital Library](https://dl.acm.org/) — buscar «Scene-Aware Behavior Synthesis for Virtual Pets in Mixed Reality»; fonte acadêmica de desenho de comportamentos sensíveis à cena.

## Repositórios técnicos úteis
| Projeto | URL | Uso e licença |
|---|---|---|
| SceneView | https://github.com/SceneView/sceneview | Android AR/3D; Apache-2.0. **Base selecionada para F1.** |
| ARCore Android SDK | https://github.com/google-ar/arcore-android-sdk | Samples e rastreamento; exemplos Apache-2.0, runtime ARCore proprietário |
| ARCore Depth codelab | https://github.com/google-ar/codelab-depth-api | Occlusion/profundidade; Apache-2.0 |
| ARCore Raw Depth | https://github.com/google-ar/codelab-raw-depth-api | Percepção de profundidade; Apache-2.0 |
| MediaPipe samples | https://github.com/google-ai-edge/mediapipe-samples | Detector, segmentação, pose; exemplos Apache-2.0 |
| Unity AR Foundation samples | https://github.com/Unity-Technologies/arfoundation-samples | Referência multiplataforma, Unity Companion License |
| llama.cpp | https://github.com/ggml-org/llama.cpp | Inference offline, MIT; pesos auditados à parte |
| sherpa-onnx | https://github.com/k2-fsa/sherpa-onnx | Fala offline, Apache-2.0; modelos/vozes auditados à parte |
| Lightship VPS Demos | https://github.com/dilmerv/LightshipVPSDemos | Demos antigos em MIT; tecnologia Lightship antiga (não usar como SDK novo) |
| Niantic Spatial SDK samples | https://github.com/nianticspatial/nsdk-samples-csharp | Referência NSDK 4.x, termos Niantic específicos |
| AR Pet | https://github.com/markzhengma/ar-pet | Referência de UX; **licença não confirmada, proibido copiar** |
| smARt_pet | https://github.com/wwkb123/smARt_pet | Referência histórica AR + voz; **licença não confirmada** |

## Documentação oficial de implementação
- [SceneView — Quickstart](https://sceneview.github.io/docs/quickstart/) — versão 4.62.0, AGP 9.4+, Kotlin 2.4, JDK/JVM 21, compileSdk 37.
- [SceneView — API para agentes/assistentes](https://sceneview.github.io/llms.txt).
- [SceneView — licença](https://github.com/SceneView/sceneview/blob/main/LICENSE).
- [ARCore — dispositivos compatíveis](https://developers.google.com/ar/devices).
- [ARCore — conceitos](https://developers.google.com/ar/develop).
- [MediaPipe Tasks](https://ai.google.dev/edge/mediapipe/solutions/guide).
- [Google LiteRT](https://ai.google.dev/edge/litert).
- [OpenAI — processamento de imagens](https://platform.openai.com/docs/guides/images).
- [OpenAI — preço vigente](https://openai.com/api/pricing/).
- [Android Gradle Plugin 9.4 (compatibilidade)](https://developer.android.com/build/releases/agp-9-4-0-release-notes) — Gradle mínimo 9.6.
- [Kotlin Gradle compatibility](https://kotlinlang.org/docs/gradle-configure-project.html) — 2.4.21 não lista AGP 9.4 como faixa totalmente testada; verificar build.
- [Android Manifest e câmera](https://developer.android.com/training/permissions/requesting).
- [CameraX 1.6.2 — bibliotecas e versões estáveis](https://developer.android.com/jetpack/androidx/releases/camera) — prévia de câmera em dispositivos sem certificação ARCore; licenças AndroidX Apache-2.0.
- [CameraX — PreviewView e ciclo de vida](https://developer.android.com/media/camera/camerax/architecture) — vincular apenas a prévia ao ciclo de vida, sem gravação nem transmissão.
- [SceneView — composição transparente e TextureSurface](https://sceneview.github.io/docs/troubleshooting/) — pet renderizado sobre a imagem da câmera; ainda exige teste em telefone.

## Produtos legados / cautela
- Meta Spark encerrado para efeitos de terceiros em 2025; **não usar**.
- Lightship ARDK 3.x substituído por Niantic Spatial SDK 4.x; **não iniciar stack nova sobre ARDK 3**.
- Sceneform original Google arquivado; **não iniciar sobre Sceneform antigo**.
- Projetos sem arquivo LICENSE explícito: usar somente como ideia/referência.

## Nota de origem
Este catálogo acompanha a reprodução integral do relatório solicitado em `docs/research/2026-10-25-pesquisa-completa.md`. As descrições resumem capacidades documentadas ou analisadas, não comprovam que o código de terceiro foi executado neste repositório.
