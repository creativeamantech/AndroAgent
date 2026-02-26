# LocalAgent

A fully functional Android Agent application that can autonomously perform tasks on an Android device using a locally running LLM.

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Clean Architecture + Hilt
- **UI**: Jetpack Compose
- **LLM**: Ollama Client, Gemini Nano (On-Device stub), VLM Client (LLaVA)
- **Database**: Room (SQLCipher Encrypted)
- **Privacy**: Encrypted Storage, PII Redaction
- **Accessibility**: Full screen reading and interaction

## Setup

1.  Clone the repository.
2.  Open in Android Studio.
3.  **For Ollama**:
    - Start Ollama: `OLLAMA_HOST=0.0.0.0 ollama serve`
    - Pull model: `ollama pull llama3` (for text) and `ollama pull llava` (for vision).
4.  **For Gemini Nano**:
    - Requires supported device (e.g. Pixel 8 Pro, S24) and Google AI Edge SDK.
    - Enable AICore in Developer Options if available.
5.  Build and run on an Android Emulator or Device.
6.  **Enable Accessibility**: Settings > Accessibility > LocalAgent.

## Features

- **Multi-Agent System**:
    - **Planner**: Decomposes high-level goals into subtasks.
    - **Executor**: Executes subtasks using ReAct loop.
    - **Orchestrator**: Manages the workflow and state.
- **Vision Capabilities**:
    - `vision_read_screen`: Captures screenshot and uses VLM to identify UI elements.
    - `VisionGrounder`: Processes VLM output (implicit).
- **Plugins**:
    - Built-in `SpotifyTools` (stub).
    - Extensible `PluginLoader` architecture.
- **Memory**:
    - Encrypted task history.
    - Keyword search for similar past tasks.
- **Privacy**:
    - PII Redaction (Regex-based).
    - AES-256-GCM Encryption for storage.

## Usage

Start a chat with a command like "Play some music on Spotify" or "Check my bank balance" (uses vision).
The agent will plan the task, execute subtasks, and report progress.
