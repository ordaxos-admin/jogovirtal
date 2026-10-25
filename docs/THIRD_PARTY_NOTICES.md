# Licenças e atribuições de terceiros

Este documento é registro de proveniência; **não** relicencia direitos de terceiros nem estabelece licença sobre código próprio.

## Modelo incluído — Fox.glb

- Origem: https://github.com/KhronosGroup/glTF-Sample-Assets/tree/main/Models/Fox
- Arquivo: `Models/Fox/glTF-Binary/Fox.glb`
- Atribuição e condições conforme README do asset:
  - Modelo por PixelMannen (2014): CC0 1.0.
  - Rigging e animação por tomkranis (2014): CC BY 4.0.
  - Conversão glTF por AsoboStudio e scurest (2017): CC BY 4.0.
- Texto da licença CC BY 4.0: https://creativecommons.org/licenses/by/4.0/
- Alterações: não realizadas no asset Fox.glb original no bootstrap.
- Este modelo é **temporário de demonstração**, não a identidade visual definitiva.

## Dependências em runtime

- SceneView (`io.github.sceneview:arsceneview`): https://github.com/SceneView/sceneview — Apache License 2.0, sujeito às transitive dependencies individuais.
- ARCore Google Play Services: distribuição e condições Google; não é runtime OSS integral.
- AndroidX/Jetpack Compose: atribuições e condições conforme pacotes dependentes.

## Não incluídos

MediaPipe, LiteRT, llama.cpp, sherpa-onnx, modelos LLM, serviços OpenAI e vozes ainda **não estão integrados ao aplicativo**. Listas de pesquisa não são manifestos de dependências.

## Pendências para publicação

- Revisar relatório final de licenças transitivas Gradle e disclosures Android.
- Definir licença do código próprio junto ao mantenedor, antes de aplicar qualquer LICENCE/NOTICE amplo.
- Revisar uso dos assets e atribuições em telas de créditos.
