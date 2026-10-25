# Arquitetura e SSOT

```text
Camera + IMU ──> ARCore + SceneView ──> Spatial tracking/planes/depth ─┐
Camera sampled ──> MediaPipe/LiteRT [future] ──> Semantic perception ─┼─> WorldModel [future]
                                                                       │
                                  PetEvent ──> PetReducer ──> PetState ├─> Renderer
                                                  │                    └─> Action planner [future]
                                                  └─> PetPreferences (local persistence)
Future (optional): snapshot explicitly requested -> VLM -> validated intent -> PetEvent
```

### SSOT

- `PetState`: única representação autoritativa de humor, energia, saciedade e ações contadas.
- `PetReducer`: lógica pura/total e testável; invariantes entre 0 e 100. Nenhuma inteligência de fornecedor altera estado diretamente.
- `PetPreferences`: somente adaptador de leitura/gravação; não inventa lógica de jogo.
- Sessão AR: anchors e câmera mantidos pelo provedor nativo; estado visual Compose só espelha a referência atual.
- Futuro `WorldModel`: somente dono de entidades e superfícies com timestamp, confidence e coordenadas.

### Limites

1. Render/AR jamais espera rede, voz ou inferência visual.
2. Detector futuro com backpressure, frame sampling dinâmico e descarte de frames obsoletos.
3. Inferência generativa só para eventos de alto nível; JSON de decisão com intents enumeradas e validação de precondições.
4. Memória local guarda fatos, não frames ou gravação ambiental.
5. Permissões antes da câmera; nenhuma credencial hardcoded no app.
6. Não misturar API proprietária com entidades de domínio (usar adapters quando adicionados).
7. Componentes do Android AR são plataforma, não a lógica do pet; futura porta iOS compartilha contratos/testes de comportamento.

### MVP atual

O primeiro commit integra as três camadas mínimas (UI AR / reducer / persistence). `WorldModel`, detector, navegação, voz e LLM ainda não têm implementação; não criar stubs vazios ou features falsas só para parecer completo.
