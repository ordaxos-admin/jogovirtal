# Diagnóstico de inicialização AR — JogoVirtal

Primeiro relato real de usuário: **"Couldn't start AR"** no APK v0.1.0-dev.1 (2026-10-25).

## Contexto e causa

A versão dev.1 declarava `android.hardware.camera.ar required=true` e `com.google.ar.core=required`. Um APK instalado por fora da Play Store não consegue garantir suporte do aparelho ou instalação do pacote Google Play Services for AR. Isso **não prova** que o celular do relato é incompatível: sem logcat/modelo, as causas exatas continuam indeterminadas.

Possíveis causas: ARCore não certificado no modelo, Serviços do Google Play para RA ausentes ou desatualizados, câmera sem permissão/em uso, instalação/perfil AR incompleto, erro de inicialização nativo.

## Correção de produto (dev.2)

- Manifest `AR Optional`: requisitos de câmera e RA opcionais; RA não bloqueia o aplicativo inteiro.
- `ArCoreApk.checkAvailability` antes de iniciar a sessão.
- Se ARCore não existir ou não puder iniciar, exibir o animal em **modo 3D sem câmera**, mantendo seu estado local, botões de cuidado e interação.
- Quando o serviço de RA estiver ausente/desatualizado, fornecer link à Google Play Store e opção de verificar novamente.
- Se sessão falhar, gravar a classe do erro em Logcat com tag `JogoVirtal` e oferecer fallback, em vez de ficar numa tela de erro.
- ARCore e SceneView continuam opcionais do ponto de vista do uso: render 3D funciona sem sessão AR. Quando AR está disponível, a experiência de ancoragem original permanece.

## Protocolo de prova

1. Em aparelho com ARCore certificado, testar primeira execução, câmera, planos, âncoras, toque, suspensão/reinício.
2. Em aparelho sem ARCore, confirmar carregamento do modo 3D **sem acesso à câmera** e sem tela AR fatal.
3. Em aparelho com serviços AR desatualizados, confirmar instrução de instalação/atualização e retentativa.
4. Medir desempenho e testar troca de modo; confirmar estado do pet persistente.
5. Se falhar, coletar logcat filtrado `JogoVirtal|ARCore|SceneView`, versão Android, fabricante/modelo e estado do Play Services for AR. **Não coletar fotos nem vídeo da casa**.

Com aparelho conectado via USB e depuração autorizada:
```powershell
adb devices -l
adb shell getprop ro.product.model
adb shell pm list packages com.google.ar.core
adb logcat -d -s JogoVirtal:E ARCore:E SceneView:E
```

Fontes: https://developers.google.com/ar/develop/java/enable-arcore e https://developers.google.com/ar/devices e https://sceneview.github.io/docs/troubleshooting/

**Estado do relato:** sem modelo do celular e sem dispositivo ADB disponível na máquina de build. Diagnóstico exato não confirmado.


## Evidência da execução no celular (25/10/2026)

O usuário enviou uma captura da versão 0.1.1-dev.2 com status explícito `UNSUPPORTED_DEVICE_NOT_CAPABLE`: a detecção ARCore, a transição para modo 3D e o render do asset original foram observados. A captura também revelou falhas de apresentação: fundo preto, pet pequeno, diagnóstico longo ocupando o topo e botão de retentativa sobre a área de navegação.

A correção 0.1.2-dev.3 inclui:
- Cena 3D maior, com fundo degradê e controles que respeitam as barras de sistema.
- Diagnóstico técnico oculto sob 'Detalhes'; a incompatibilidade deixa de dominar a tela.
- Prévia de câmera alternativa via **CameraX 1.6.2** em aparelhos sem ARCore, com bichinho 3D sobreposto em transparência. Ela não detecta planos, não ancora o pet no mundo e não deve ser chamada de RA espacial.
- Modo 3D offline continua independente da câmera; o pet compartilha a mesma persistência.
- Prévia libera o vínculo da câmera ao sair da tela. Não há captura/salvamento/transmissão das imagens.

**Critério pendente:** o render composto CameraX + SceneView TextureSurface e o posicionamento visual ainda exigem verificação em aparelho real; testes Kotlin/Gradle não demonstram que as duas superfícies se sobrepõem corretamente em todas as GPUs.
