# Pesquisa e referências técnicas — consultadas em outubro de 2026

Consulte o [relatório integral](research/relatorio-bichinho-ar-ia.md) para produtos, riscos e comparativos. Catálogo para embasar código do projeto:

| Fonte | Link | Aplicação | Licença / alerta |
|---|---|---|---|
| SceneView | https://github.com/SceneView/sceneview | AR declarativo Android, futuro iOS | Apache-2.0; tracking depende ARCore/ARKit |
| SceneView quickstart | https://github.com/SceneView/sceneview/blob/main/docs/docs/quickstart.md | Toolchain Android 4.62.0 | documentação oficial |
| SceneView AR codelab | https://github.com/SceneView/sceneview/blob/main/docs/docs/codelabs/codelab-ar-compose.md | Planes, anchors e depth | documentação oficial |
| ARCore SDK | https://github.com/google-ar/arcore-android-sdk | ARCore Android examples | exemplos Apache-2.0; serviço ARCore é proprietário |
| ARCore Depth Codelab | https://github.com/google-ar/codelab-depth-api | Oclusão e profundidade | Apache-2.0 |
| MediaPipe Samples | https://github.com/google-ai-edge/mediapipe-samples | Detecção objetos/pessoas (M3) | exemplos Apache-2.0 |
| LiteRT | https://ai.google.dev/edge/litert | Execução ML local (M3/M5) | auditoria por runtime e pesos |
| Unity AR Foundation Samples | https://github.com/Unity-Technologies/arfoundation-samples | Comparação multiplataforma | Unity Companion License, não MIT |
| AR Pet | https://github.com/markzhengma/ar-pet | Exemplo conceitual pet AR | não copiar sem licença confirmada |
| smARt_pet | https://github.com/wwkb123/smARt_pet | Exemplo AR pet com IA/voz | licença a confirmar |
| Niantic Spatial SDK samples | https://github.com/nianticspatial/nsdk-samples-csharp | Depth, spatial semantics | termos Niantic, não presumir OSS |
| llama.cpp | https://github.com/ggml-org/llama.cpp | LLM local futuro | MIT; pesos com licenças próprias |
| sherpa-onnx | https://github.com/k2-fsa/sherpa-onnx | STT/TTS offline futuro | Apache-2.0; auditar vozes |
| glTF Fox sample | https://github.com/KhronosGroup/glTF-Sample-Assets/tree/main/Models/Fox | Raposa demonstrativa AR | malha CC0; rig/anim/conversão CC-BY-4.0 |
| ARKit | https://developer.apple.com/augmented-reality/arkit/ | Futuro iOS | Apple proprietary |
| Pokémon GO | https://pokemongolive.com/ | Referência AR em ambiente real | produto comercial |
| Peridot | https://playperidot.com/ | Referência de pet AR contextual | produto comercial |

### Mudanças de ecossistema importantes

- Em 2026, Niantic Lightship ARDK 3.x é legado; usar Niantic Spatial SDK 4.x caso essa dependência se faça necessária.
- Meta Spark Effects para terceiros foi encerrado em 2025; não escolher como base.
- TensorFlow Lite é agora apresentado como LiteRT; não escolher tutoriais descontinuados como fonte única.
- Datas, versões e licenças devem ser revistas antes de cada upgrade; URL pública não equivale a licença de uso.

### Decisão

MVP: Kotlin + Jetpack Compose + SceneView/ARCore, sem Unity. MediaPipe entra só depois de validar AR e desempenho. Implementação própria, sem copiar exemplos com licença ambígua.
