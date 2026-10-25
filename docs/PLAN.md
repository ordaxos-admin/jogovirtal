# Plano de execução — JogoVirtal

Atualizado: 2026-10-25. Estado: desenvolvimento inicial; **compilação Android e testes unitários locais aprovados** (Gradle 9.8.1, JDK 21, SDK 37.0, 3/3 testes, APK debug de 46.225.351 bytes). **Teste em telefone ARCore ainda pendente.**

## Visão de produto
Um pet 3D persistente no ambiente filmado, que encontra superfícies, reage a toque e a objetos reais, mantém necessidades e personalidade e pode conversar. O loop gráfico e comportamental deve funcionar offline. IA generativa não controla a física nem recebe stream contínuo da câmera.

## Prioridades e gates de aceite
| Fase | Entrega | Gate objetivo |
|---|---|---|
| F0 — Fundação e pesquisa | Documentação, arquitetura, referências, licenças e política de privacidade; MVP Android definido | Fonte da verdade nos arquivos de docs; dependências identificadas |
| F1 — Primeiro pet em AR | APK Android, permissão de câmera, plano real, pet GLB criado pelo próprio projeto, âncora após toque, botão de carinho | Teste no aparelho compatível com ARCore; funcionamento sem internet; teste unitário de estado |
| F2 — Mundo persistente | Persistência de humor/energia, reposicionamento, UI de alimentação, fluxo de reinício, ciclo de animações | Dados sobrevivem reinício; ausência de estado duplicado; ausência de vazamento de anchors |
| F3 — Entendimento local | Interface PerceptionProvider; MediaPipe/LiteRT para poucas classes; objeto->evento semântico | Desempenho mensurado, descarte de frames, câmera não sai do aparelho |
| F4 — Presença espacial | Depth/occlusion em aparelhos capazes, limites de navegação, degradação graciosa | Pet não atravessa grosseiramente móveis no roteiro de QA |
| F5 — Personalidade e IA | Contratos de intent JSON validados, consentimento explícito, VLM por snapshot e LLM apenas por evento, custos/quotas | Offline funcional; sem chaves no cliente; política de retenção e teste de falhas |
| F6 — Experiência de produto | Voz local, tutorial, compatibilidade, acessibilidade, arte/animações, iOS planejado | Medição de bateria/fps, publicação somente após QA |

## Recorte técnico inicial
- Android primeiro, Kotlin + Jetpack Compose + SceneView 4.62.0 / ARCore.
- JDK 21, AGP 9.4.1, Gradle 9.8.1, Kotlin/Compose 2.4.21, compileSdk 37, minSdk 24.
- AR requerido na primeira vertical slice. O modo sem AR e iOS entram após prova de F1.
- Pet gerado originalmente por script local, com arquivo GLB versionado e sem asset de terceiros.
- PetState/PetRules puros, camada UI desacoplada; nenhuma API cloud na F1.
- Use o menor conjunto de dependências possível.

## Ordem imediata
1. Salvar a pesquisa integral e uma lista auditável de referências/licenças.
2. Criar scaffold e pet original, API do domínio e testes.
3. Rodar build + testes em ambiente com JDK 21, Android SDK 37 e Gradle 9.8.1; se ausentes, registrar bloqueio (não inventar aprovação).
4. Testar abertura, permissão, plano, inserção do pet e reação ao toque em dispositivo Android ARCore.
5. Corrigir falhas estruturais antes de ampliar escopo.

## Definição de concluído
"F1 concluída" requer APK compilado, testes automatizados passando e execução observada no hardware. Apenas código commitado **não** significa funcionalidade validada. Para cada fase, atualizar este arquivo e abrir PR somente quando necessário, com evidência objetiva.

## Não fazer
- Não portar Unity, Godot, Flutter ou React Native junto no primeiro ciclo.
- Não instalar LLM grande ou enviar vídeo continuamente para nuvem.
- Não implementar física/colisão como delegação a um LLM.
- Não criar dados falsos de compatibilidade, métricas, testes ou custos.
- Não copiar pet 3D ou código de outros jogos sem licença expressa.

## Bloqueio da publica��o de workflow
A credencial de publica��o atual n�o possui escopo OAuth de workflows. O YAML candidato est� em docs/ci/github-actions-android.yml; n�o est� ativo em GitHub Actions. Ativar ap�s autoriza��o adequada e validar assembleDebug e testes no CI.
