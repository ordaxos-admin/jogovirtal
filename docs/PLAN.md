# Plano de engenharia — JogoVirtal

Data-base: 2026-10-25. Este documento separa **entregue no código**, **validado** e **planejado**. Nunca presumir conclusão a partir apenas de um commit.

## Meta

Um animal virtual que existe no espaço real: detecta piso/mesa, reage a interação humana, possui memória/necessidades e, em etapas posteriores, percebe objetos e conversa sobre o contexto. Android-first para provar a experiência em hardware modesto; portar depois sem duplicar o motor.

## Marcos / Definition of Done

| Marco | Entrega | Validação obrigatória | Estado |
|---|---|---|---|
| M0 Fundação | Repositório, documentação, bibliografia, matriz jurídica, projeto Android, asset de teste e testes unitários | Arquivos versionados, testes da lógica, tentativa de build | Em implementação |
| M1 AR jogável | Permissão de câmera, planos e âncora; modelo pet; ações tocar/alimentar/descansar; estado persistente | Build APK, instalação e vídeo de teste em **celular ARCore** | Pendente de dispositivo |
| M2 Mundo físico | Depth quando disponível, oclusão visual e deslocamento seguro; fallback quando indisponível | Testes em pelo menos um aparelho sem sensor depth avançado | Planejado |
| M3 Percepção | MediaPipe/LiteRT para 3 classes úteis (pessoa, bola, cadeira), frames amostrados; world model | Latência, precisão e bateria medidas, offline | Planejado |
| M4 Personalidade | FSM/árvore de comportamento, emoções e preferências; memória local com migrações de schema | Testes de transições, restauração após matar/reabrir app | Planejado |
| M5 IA opcional | Provedor substituível (cloud/local), snapshot somente por consentimento, intents JSON validados | Sem vídeo contínuo, offline degradado, ações não inventadas | Planejado |
| M6 Voz e beta | Voz local, interação por fala e ajustes visuais; QA e políticas de privacidade | Benchmarks FPS/bateria, acessibilidade, consentimento | Planejado |
| M7 iOS | Interface nativa ARKit/RealityKit com mesmo contrato do pet | Teste em iPhone real; sem fork do estado/decisões | Futuro |

## Ordem de trabalho imediato

1. Validar o projeto Gradle em ambiente com JDK 21 / SDK 37.
2. Instalar em celular ARCore para provar permissão, plano, anchor, escala e animação.
3. Resolver eventuais incompatibilidades SceneView, apenas pela causa raiz.
4. Adicionar testes instrumentados e medição de início, FPS e memória.
5. Avançar para depth/occlusion antes de qualquer LLM.

## Restrições / riscos

- ARCore principal é serviço do Google (proprietário), apesar de exemplos Apache-2.0. Não prometer pilha inteiramente OSS.
- SceneView 4.62.0 exige toolchain de 2026: AGP 9.4+, Kotlin 2.4+, JVM 21, compileSdk 37.
- Dispositivos, sombras, relocalização, calor, bateria e oclusão diferem entre modelos.
- Assets 3D têm licença independente; não empacotar mídia de terceiros sem atribuição.
- Não oferecer o app como seguro para público infantil antes de revisão especializada.

## Regra de implementação

Um dono para cada estado: session tracking do ARCore; entidades espaciais do WorldModel; humor/necessidades do PetReducer; persistência do PetPreferences; provider de IA não altera diretamente estas estruturas. Telemetria da câmera desligada por padrão.
