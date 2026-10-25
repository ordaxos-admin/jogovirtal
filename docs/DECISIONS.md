# Decisões registradas (ADRs)

## ADR-001 — Android-first com SceneView + ARCore

**Situação: aceito para o bootstrap.** Razão: base permissiva (SceneView Apache-2.0), APIs AR modernas e fluxo rápido em Kotlin Compose. Custo: serviço ARCore é proprietário e suportado apenas em aparelhos compatíveis. iOS será um adapter futuro, não duplicação do motor do pet.

## ADR-002 — PetReducer puro e persistência local

**Situação: aceito.** Estado e necessidades não dependem do render ou de APIs de LLM. Eventos enumerados evitam dupla autoria. SharedPreferences é suficiente para o primeiro marco; troca futura por banco local só por migração explícita do estado.

## ADR-003 — IA não entra no loop gráfico

**Situação: aceito.** Um pet deve responder a toque, mover e persistir offline. Percepção local e LLM eventual têm cadências e subsistemas separados.

## ADR-004 — Privacidade por padrão

**Situação: aceito.** App não envia câmera nem coleta frames neste bootstrap. IA futura exige controle/consentimento e só recebe snapshots pontuais necessários.

## ADR-005 — Assets demonstrativos com atribuição

**Situação: aceito.** Fox.glb é amostra de terceiros e é rastreado em THIRD_PARTY_NOTICES.md; não copiar nem publicar como personagem exclusivo.

## ADR-006 — Licença do código próprio

**Situação: pendente da decisão do proprietário.** Dependências open source não significam que o autor licenciou automaticamente sua própria base.
