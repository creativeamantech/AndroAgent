# LocalAgent

A fully functional, production-grade Android Agent application that can autonomously perform tasks on an Android device using a locally running LLM.

## Features

- **Local LLM Execution**: Connects to Ollama (default) or other local backends.
- **Accessibility Service**: Reads screen content and performs gestures (tap, swipe).
- **Vision-Language Model (VLM)**: Uses LLaVA/MiniCPM-V to analyze screenshots when accessibility tree is insufficient.
- **Multi-Agent Architecture**: Includes a Planner and Executor agent for complex tasks.
- **Memory**: Persists task history and learns from past executions.
- **Privacy**: PII Redaction and Encryption stubs for sensitive data.
- **Plugin System**: Extensible architecture for custom tools (includes Home Assistant stub).
- **Model Manager**: Download and manage Ollama models directly from the app.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material3)
- **Architecture**: MVVM + Clean Architecture + Hilt
- **Networking**: Retrofit + OkHttp
- **Database**: Room
- **Async**: Coroutines + Flow

## Setup & Usage

1.  **Prerequisites**:
    - Android Device (Android 8.0+) or Emulator.
    - Ollama server running on your local machine or network.
    - Run `ollama serve` on your machine.
    - Pull required models:
      ```bash
      ollama pull llama3
      ollama pull llava
      ```

2.  **Configuration**:
    - Open the app.
    - Go to **Settings**.
    - Set the **Ollama Base URL** (e.g., `http://10.0.2.2:11434` for emulator, or your LAN IP for device).
    - Toggle **Multi-Agent Mode** if desired.

3.  **Permissions**:
    - Enable **LocalAgent Accessibility Service** in Android Settings when prompted or manually.
    - Grant other runtime permissions as requested.

4.  **Using the Agent**:
    - Type a command in the chat (e.g., "Open Settings", "Read my screen", "Plan a trip").
    - The agent will think, plan (if multi-agent), and execute actions.
    - Use the **History** icon to view past tasks.
    - Use **Manage Models** in Settings to pull new models.

## Development

- **Tools**: `ToolsModule.kt` registers available tools. Add new `AgentTool` implementations here.
- **Plugins**: `PluginRegistry.kt` loads plugins. Implement `AgentTool` for your plugin.
- **LLM**: `OllamaClient.kt` handles communication. Implement `LLMClient` for other backends.

## License

MIT
