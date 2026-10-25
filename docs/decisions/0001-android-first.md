# ADR-0001: MVP nativo Android, IA fora do loop gráfico

Data: 2026-10-25 — status: aceito para início do desenvolvimento.

## Contexto
A pesquisa mostrou três complexidades distintas: AR espacial, visão computacional e comportamento persistente. Misturar IA generativa com render gera latência, custo, exposição da câmera e fragilidade offline.

## Decisão
Começar com Kotlin + Compose + SceneView/ARCore; manter PetState/PetRules independentes. Adicionar MediaPipe/LiteRT posteriormente como provedor local substituível. Modelo generativo só pode sugerir ações enumeradas e validadas, não controlar movimentação por frame. Nenhuma API cloud é incluída no MVP F1. Usar pet original gerado por script e não assets de jogos comerciais.

## Consequências
Pró: licença permissiva para código nosso e grande parte da base; privacidade local e arranque leve. Contra: dependência de ARCore (plataforma proprietária) e necessidade de futura implementação Apple. A primeira etapa requer SDK Android 37 e JDK 21 por causa de SceneView 4.62.0, e aparelho físico para validação.
