# LocalAgent

A fully functional Android Agent application that can autonomously perform tasks on an Android device using a locally running LLM.

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Clean Architecture + Hilt
- **UI**: Jetpack Compose
- **LLM**: Ollama Client
- **Database**: Room
- **Privacy**: Encrypted Storage, PII Redaction

## Setup

1.  Clone the repository.
2.  Open in Android Studio.
3.  Ensure you have an Ollama server running locally (default: `http://localhost:11434`) or configure the URL in `OllamaClient.kt`.
4.  Build and run on an Android Emulator or Device.

## Features implemented

- **Core Agent Logic**: ReAct loop, Tool Registry.
- **LLM Integration**: Ollama Client implementation.
- **Memory**: Task history storage with Room.
- **Privacy**: PII Redaction and Encryption stubs.
- **UI**: Chat interface and Settings screen.
- **Accessibility**: Stubbed Accessibility Service integration.

## Note

This project is scaffolded based on the `android_agent_prompt.md`. It requires a running LLM backend to function fully. The Accessibility Service needs to be enabled manually in Android Settings.
