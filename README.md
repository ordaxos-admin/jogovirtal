# JogoVirtal 🐾

Pet virtual em realidade aumentada que existe na câmera do celular, percebe superfícies e evolui para comportamentos inteligentes com IA opcional.

**Estado atual: protótipo Android em desenvolvimento.** O usuário comprovou em celular o fallback 3D por incompatibilidade ARCore (captura em 25/10/2026). A prévia alternativa CameraX + pet 3D e a interface revisada exigem confirmação no aparelho; o modo AR espacial segue sem teste em celular compatível. O APK é técnico, não uma versão final.

## Para quê?
O primeiro objetivo é um pet 3D original ancorado em um plano real, com interação por toque, humor/energia persistentes e funcionamento offline. Depois entram reconhecimento de objetos, profundidade/occlusion, navegação, conversa e voz.

## Stack
Kotlin + Jetpack Compose + [SceneView](https://github.com/SceneView/sceneview) 4.62.0 + ARCore (opcional) + CameraX 1.6.2 para prévia sem ARCore. Android-first, minSdk 24, compileSdk 37, JDK 21, AGP 9.4.1. Pacotes com versões fixadas. Pet 3D gerado no próprio repositório, sem modelos importados de outras obras.

## Organização
- `app/` — aplicativo Android, interface AR, estado local e testes.
- `scripts/generate-pet.mjs` — fonte reprodutível do asset 3D original.
- `docs/PLAN.md` — fases, critérios de aceite e status.
- `docs/ARCHITECTURE.md` — responsabilidades e privacidade.
- `docs/research/2026-10-25-pesquisa-completa.md` — relatório integral.
- `docs/research/REFERENCES.md` — bibliotecas, referências e ressalvas de licença.
- `docs/decisions/` — decisões arquiteturais.
- `docs/TROUBLESHOOTING-AR.md` — diagnóstico de ARCore e modo 3D de compatibilidade.

## Executar (requer Android Studio / SDK 37 / JDK 21)
1. Abrir a pasta raiz no Android Studio compatível com AGP 9.4.
2. Rodar `./gradlew :app:assembleDebug :app:testDebugUnitTest` (no Windows, `gradlew.bat`).
3. Instalar em Android com API 24+; quando o aparelho tiver ARCore, autorizar a câmera para o modo AR. Se não tiver, o jogo abre o bichinho em modo 3D.
4. No modo AR, apontar para uma superfície e tocar para posicionar; no modo 3D, manipular o bichinho sem câmera. O botão **Ver bichinho pela câmera** permite sobrepor o modelo 3D à imagem real em dispositivos sem ARCore, sem rastreamento espacial.
5. Usar o botão de carinho para alterar humor e energia; reiniciar e verificar persistência.

O teste real é obrigatório; a imagem do APK e a compatibilidade dependem do hardware e SDK.

## Regra de privacidade
A versão inicial **não usa API de IA e não envia o vídeo da câmera** para nenhum serviço. Uma futura visão/cloud deverá ter consentimento, requisição discreta por evento e sem chave secreta embarcada.

## Desenvolvimento
Projeto em fase inicial. Leia [AGENTS.md](AGENTS.md), [Plano](docs/PLAN.md) e [Arquitetura](docs/ARCHITECTURE.md). Não copiar assets externos sem verificar licença.

## Licenças
Código deste projeto licenciado sob Apache-2.0 em `LICENSE`. Isso não cobre automaticamente as licenças do ARCore, pesos de IA, vozes ou outros SDKs externos.
