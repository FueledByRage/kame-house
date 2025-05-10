# 🤖 Kame Way - Pose Detection com Flutter + Kotlin

Este é um projeto mobile que combina **Flutter** (para a interface) com **Kotlin/Java** (para recursos nativos do Android), utilizando **Machine Learning** para detectar a pose do corpo em tempo real usando a câmera do celular.

Machine learning no lado do cliente significa que a inteligência artificial roda diretamente no dispositivo do usuário — como um celular — sem precisar enviar dados para servidores externos. Isso traz mais velocidade, privacidade e funciona mesmo sem internet. No caso deste app, a detecção de poses é feita diretamente no aparelho usando a câmera e um modelo de IA leve, garantindo uma experiência em tempo real.

---

## 💡 O que o app faz

- Mostra a imagem da câmera ao vivo na tela do app
- Detecta pontos do corpo (como cabeça, ombros, mãos e pés)
- Desenha esses pontos por cima da imagem, em tempo real
- Permite alternar entre câmera frontal e traseira com um botão

Tudo isso usando um modelo de **inteligência artificial embarcado no dispositivo**, sem necessidade de conexão com a internet.

---

## 🛠️ Tecnologias utilizadas

- **Flutter**: para construir a interface do aplicativo
- **Kotlin / Java**: para acessar a câmera e processar imagens no Android (Java 11)
- **ML Kit (ou MediaPipe)**: biblioteca de machine learning do Google usada para detectar poses

---

## 🚀 Como rodar

1. Tenha o Flutter instalado na sua máquina
2. Conecte um celular Android (ou use um emulador que suporte câmera real)
3. No terminal, rode os comandos:

```bash
flutter pub get
flutter run
```

📄 Licença
Distribuído sob a licença MIT.

