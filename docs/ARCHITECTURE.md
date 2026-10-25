# Arquitetura — JogoVirtal

## Pipeline e ownership
```text
Câmera e IMU
 ├─ ARCore/SceneView: tracking, planos, anchors, profundidade (opcional)
 └─ PerceptionProvider: detector local futuro (MediaPipe/LiteRT)
       ↓
 WorldModel: superfícies/objetos + confiança + validade temporal
       ↓
 PetState (único estado persistente) + PetRules (motor local determinístico)
       ↓
 ActionValidator → AR renderer / animação / áudio
       ↑
 DialogueProvider opcional → apenas intenções tipadas, nunca controles gráficos por frame
```

## Contratos
- **SpatialProvider**: posição, superfícies, profundidade, capacidade do aparelho. Implementação inicial SceneView/ARCore.
- **PerceptionProvider**: classes/máscaras/confiança, inferência fora do loop de render e sem fila ilimitada. **Ainda não implementado**.
- **PetState/PetRules**: necessidades e decisão de comportamento local; regras puras, testáveis, sem SDK AR.
- **PetRepository**: armazenamento local; contrato persistente evolui para SQLite quando houver mais tipos de dado.
- **DialogueProvider**: comandos de alto nível com whitelist e validação espacial; **não implementado**.
- **VoiceProvider**: TTS local inicialmente; **não implementado**.
- **AR UI**: desenha o pet, captura gestos e espelha PetState, sem criar um segundo cérebro.

## Rendimentos e isolamento
- Rendering/tracking meta: 30–60 FPS (alvo, não garantia).
- Percepção futura: taxa adaptativa inicialmente 5–15 Hz; processar apenas último frame disponível.
- IA de diálogo: evento discreto, nunca chamada síncrona no render.
- Habilitar fallback quando depth não existir; não exigir LiDAR.
- Modelos, pesos, voz e assets exigem auditoria de licença independente de bibliotecas.

## Privacidade
- Captura autorizada no sistema; sem gravação automática.
- IA remota desabilitada por padrão e não presente na F1.
- Se houver visão remota futura: consentimento claro e só crop/snapshot estritamente necessário, jamais stream de ambiente.
- Não armazenar frames crus nem credenciais no aplicativo.
- As políticas Google Play/Apple para câmeras e público infantil exigem revisão antes da distribuição.

## Desvios conhecidos no scaffold
- A lib SceneView e ARCore contêm dependências de plataforma não abertas; apenas nosso código e as libs explicitamente licenciadas são OSS.
- Sem Android SDK e Java no computador de bootstrap, não se pode afirmar compilação; CI e prova em telefone são os gates de F1.
