# Diretrizes para contribuições e agentes

- Usar `docs/PLAN.md`, `docs/ARCHITECTURE.md` e os contratos do código como fontes de verdade.
- Evitar duplicação, protótipos paralelos, APIs desnecessárias, dependências legadas e paliativos.
- Fazer uma fase vertical por vez; não afirmar funcionalidade ou testes não executados.
- O estado do pet e regras de decisão são locais, não pertencem ao LLM nem à interface AR.
- Nenhum dado de câmera deve ser transmitido de forma contínua ou sem consentimento específico.
- Atualizar `docs/research/REFERENCES.md` quando incorporar bibliotecas, modelos ou assets.
- Não copiar material de projetos sem licença explícita; respeitar termos de SDKs proprietários.
- Sempre validar `assembleDebug`, `testDebugUnitTest` e um telefone compatível com ARCore antes de declarar a fase de AR concluída.
- Builds e SDKs podem ter versões estritas: confirmar requisitos oficiais, não baixar versão de forma silenciosa.
- PRs pequenas só quando há entregas independentes reais; terminar o fluxo ponta a ponta.
