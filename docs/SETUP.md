# Rodar o bootstrap Android

## Ferramentas

- Android Studio com suporte ao Android Gradle Plugin 9.4.1
- JDK 21
- Android SDK Platform 37, build-tools compatíveis
- Android com ARCore, câmera e Google Play Services for AR

Versões alinhadas a SceneView 4.62.0: https://github.com/SceneView/sceneview/blob/main/docs/docs/quickstart.md

## Fluxo

1. Abra esta raiz no Android Studio (é o projeto Gradle; não há subprojeto legado).
2. Permita sincronizar as dependências e baixe o SDK 37.
3. Execute `./gradlew testDebugUnitTest` (ou `gradlew.bat` no Windows) após gerar/verificar o wrapper.
4. Execute o `app` em dispositivo físico compatível com ARCore.
5. Autorize câmera, mova lentamente o dispositivo e aponte para piso ou mesa em ambiente iluminado.
6. O modelo demonstra o posicionamento. Toque na tela para acariciar, ou use botões para alimentar e descansar.

## Limitações conhecidas

- O build APK não é comprovado até executar SDK/Gradle em ambiente real; verifique o estado em CI.
- A raposa é um asset de exemplo; animação e navegação física autônomas ainda não estão implementadas.
- Nem todo celular fornece Depth API; o app desativa depth quando indisponível.
- Este app não contém nem solicita chave de API de IA.
