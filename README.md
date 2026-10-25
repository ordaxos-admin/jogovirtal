# JogoVirtal

**Pet virtual de realidade aumentada, com percepção local e IA opcional.**

O celular usa a câmera para detectar superfícies e colocar um animal 3D no ambiente real. A evolução do projeto inclui objetos reais, obstáculos, memória, personalidade e conversa contextual.

## Status (25/10/2026)

**Bootstrap / protótipo Android.** O código AR inicial, um modelo de raposa de demonstração e testes do motor de comportamento fazem parte do primeiro marco. Ainda **não é um jogo pronto**, nem foi comprovado num aparelho ARCore real. Não há visão semântica, voz nem API de IA integrada neste marco.

## Primeira plataforma

Android nativo com Kotlin, Jetpack Compose, SceneView 4.62.0 e ARCore. A camada do pet é independente do renderer e de provedores de IA. Compatibilidade com iOS é objetivo posterior, não uma promessa de binário multiplataforma agora.

## Experiência inicial

- Solicita permissão de câmera e orienta a apontá-la para uma superfície iluminada.
- Detecta plano horizontal e ancora um modelo de raposa no mundo real.
- Usa profundidade quando o dispositivo oferece suporte.
- Usuário pode acariciar, alimentar ou fazer o bichinho descansar; humor e energia são persistidos localmente.
- Funciona sem conta e sem chave de API. **Não transmite vídeo.**

## Desenvolvimento

Precisa de JDK 21, Android SDK 37, Android Gradle Plugin 9.4.1+ e celular compatível com ARCore. Consulte [como executar](docs/SETUP.md).

## Documentação

- [Pesquisa completa](docs/research/relatorio-bichinho-ar-ia.md)
- [Plano e critérios de aceite](docs/PLAN.md)
- [Arquitetura e SSOT](docs/ARCHITECTURE.md)
- [Referências, códigos e licenças](docs/REFERENCES.md)
- [Terceiros e assets](docs/THIRD_PARTY_NOTICES.md)
- [Decisões técnicas](docs/DECISIONS.md)

## Filosofia

Processamento local por padrão. Tracking/renderização, percepção semântica e raciocínio generativo são subsistemas independentes. O LLM não controla física nem rendering por frame. O estado do pet possui um único proprietário (PetReducer + PetPreferences); sem duplicações, sem serviços desnecessários.

> Licenciamento do **código próprio** do projeto ainda não foi definido pelo mantenedor. Licenças abertas de dependências e modelos de terceiros não concedem uma licença automática sobre o aplicativo.
