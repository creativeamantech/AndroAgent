# LocalAgent

A fully functional Android Agent application that can autonomously perform tasks on an Android device using a locally running LLM.

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Clean Architecture + Hilt
- **UI**: Jetpack Compose
- **LLM**: Ollama Client (default: `http://10.0.2.2:11434` for Emulator)
- **Database**: Room
- **Privacy**: Encrypted Storage (AES-256-GCM + Android Keystore), PII Redaction
- **Accessibility**: Full screen reading and interaction (tap, swipe, type, etc.)

## Setup

1.  Clone the repository.
2.  Open in Android Studio.
3.  Ensure you have an Ollama server running locally.
    - Start Ollama: `OLLAMA_HOST=0.0.0.0 ollama serve`
    - Pull model: `ollama pull llama3`
4.  Build and run on an Android Emulator or Device.
5.  **Important**: Enable "LocalAgent" in Android Settings > Accessibility.

## Features implemented

- **Core Agent Logic**: ReAct loop (Observe-Think-Act), Tool Registry.
- **LLM Integration**: Ollama Client implementation (configurable).
- **Memory**: Task history storage with Room.
- **Privacy**: PII Redaction and Encryption Manager (AES-256-GCM).
- **UI**: Chat interface and Settings screen.
- **Accessibility**: Real-time screen reading (JSON tree) and interaction tools.
- **Tools**: `read_screen`, `tap`, `swipe`, `type_text`, `press_key`, `done`.

## Usage

Start a chat with a command like "Open Settings" or "Tap on the search bar". The agent will:
1.  Read the screen.
2.  Plan an action.
3.  Execute the tool via Accessibility Service.
4.  Repeat until done.

## Note

This project is scaffolded based on the `android_agent_prompt.md`. It requires a running LLM backend to function fully. The Accessibility Service needs to be enabled manually in Android Settings.
