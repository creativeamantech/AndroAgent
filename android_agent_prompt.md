# Prompt: Android AI Agent App with Local LLM

> Use this prompt with any AI coding assistant (Claude, Cursor, GPT-4, etc.) to scaffold a fully functional Android Agent powered by a local LLM.

---

## 🤖 The Prompt

```
You are an expert Android developer specializing in AI-powered agentic systems.
Build a fully functional, production-grade Android Agent application in Kotlin
called "LocalAgent" that can autonomously perform tasks on an Android device
using a locally running LLM (e.g., Llama 3, Mistral, Phi-3, or Gemma via
llama.cpp or Ollama) — with optional full on-device inference, VLM-based screen
understanding, multi-agent coordination, plugin extensibility, persistent memory,
and end-to-end encrypted privacy controls.

---

## PROJECT OVERVIEW

Create an Android app called "LocalAgent" that:
- Accepts natural language commands from the user (text or voice)
- Sends the command to a local LLM (running on-device OR on local network)
- Parses the LLM's response as structured tool-calling actions
- Executes those actions on the Android device via Accessibility Services or Android APIs
- Optionally uses a Vision-Language Model (VLM) to understand the screen via screenshots
- Observes the result and feeds it back to the LLM (ReAct / Observe-Think-Act loop)
- Repeats until the task is complete or the LLM returns a DONE signal
- Supports a Planner + Executor multi-agent architecture for complex tasks
- Persists task memory across sessions and learns from past executions
- Stores all logs and screenshots with AES-256 encryption and PII redaction

---

## TECH STACK

- Language: Kotlin
- Min SDK: 26 (Android 8.0), Target SDK: 34
- Architecture: MVVM + Clean Architecture
- LLM backends (user-selectable):
  - Ollama REST API (localhost:11434) — recommended for network mode
  - llama.cpp HTTP server (localhost:8080)
  - On-device via MLC LLM (Vulkan/OpenCL GPU inference)
  - On-device via llama.cpp Android JNI bindings
  - On-device via MediaPipe LLM Inference API (Gemma)
- VLM backend: LLaVA or MiniCPM-V via Ollama (for screenshot-based understanding)
- UI: Jetpack Compose
- DI: Hilt
- Networking: Retrofit + OkHttp (SSE streaming support)
- Database: Room DB + SQLCipher (AES-256 encrypted)
- Coroutines + Flow for async
- Accessibility Service for UI interaction and screen reading
- Android APIs: Intent system, ContentResolver, NotificationManager, etc.
- Security: Android Keystore, AES-256-GCM, on-device NER for PII redaction

---

## CORE COMPONENTS TO BUILD

### 1. LLM Client Module (Multi-Backend)

Build a unified `LLMClient` interface with multiple swappable implementations:

interface LLMClient {
    suspend fun chat(messages: List<Message>, stream: Boolean): Flow<String>
    fun isAvailable(): Boolean
    val backendName: String
}

Implement the following concrete clients, all behind this interface:
- `OllamaClient` — connects to Ollama REST API, supports SSE streaming
- `LlamaCppClient` — connects to llama.cpp HTTP server
- `OnDeviceMLCClient` — loads a GGUF model via MLC LLM JNI bindings, runs on GPU
- `OnDeviceLlamaCppClient` — runs llama.cpp natively on device via JNI, CPU/GPU
- `MediaPipeGemmaClient` — uses Google's MediaPipe LLM Inference API for on-device Gemma

Add an `LLMClientFactory` that returns the correct client based on user settings.
Add a model manager screen where users can download GGUF model files, switch between
models, delete them, and view benchmarked token/s performance per model.

Include a `VLMClient` specifically for vision tasks:
- Sends base64-encoded screenshots to a local VLM server (e.g., LLaVA via Ollama)
- Returns a structured JSON description of visible UI elements and their positions
- Falls back to VLM automatically when the accessibility tree is empty or blocked

---

### 2. Tool Registry (Built-in + Plugin Tools)

#### 2a. Built-in Tools

Define and register the following tools the LLM can call:

| Tool Name                | Description                                                      |
|--------------------------|------------------------------------------------------------------|
| `tap(x, y)`              | Tap a screen coordinate via Accessibility Service                |
| `swipe(x1,y1,x2,y2)`    | Swipe gesture on screen                                          |
| `type_text(text)`        | Type text into the focused input field                           |
| `press_key(key)`         | Press HOME, BACK, RECENTS, ENTER, etc.                           |
| `open_app(package)`      | Launch an app by package name                                    |
| `read_screen()`          | Capture current screen as accessibility tree (text-based)        |
| `vision_read_screen()`   | Capture screenshot → send to VLM → get visual element list       |
| `take_screenshot()`      | Capture and return a screenshot (base64 PNG)                     |
| `send_sms(to, msg)`      | Send an SMS message                                              |
| `make_call(number)`      | Initiate a phone call                                            |
| `set_alarm(time)`        | Set an alarm at the given time                                   |
| `search_web(query)`      | Open browser with a search query                                 |
| `get_contacts(name)`     | Look up a contact by name                                        |
| `read_notifications()`   | Read current notification shade                                  |
| `recall_memory(task)`    | Search past task memory for similar successful strategies        |
| `done(result)`           | Signal task completion with a summary result                     |

#### 2b. Custom Tool Plugin SDK

Implement a Plugin SDK so developers and users can extend the agent with custom tools
without modifying the core app.

Define an `AgentTool` interface:

interface AgentTool {
    val name: String
    val description: String
    val argsSchema: JsonObject      // JSON Schema for arguments
    suspend fun execute(args: JsonObject): ToolResult
}

- Support loading plugin APKs via Android's `PackageManager` plugin discovery pattern
- Support JSON-manifest-defined tools (simple shell/intent-based tools without code)
- Build a `PluginRegistry` that merges built-in tools and installed plugin tools
- Expose a Plugin Store UI screen: browse, install, enable/disable, and uninstall tool packs
- Example built-in plugin packs to include:
  - `HomeAssistantTools` — control smart home via HA REST API
  - `SpotifyTools` — play, pause, search music via Spotify Intents
  - `FileManagerTools` — read, write, move, delete files in external storage
  - `ShellCommandTools` (root-optional) — run adb shell commands

---

### 3. Accessibility Service

- Create `AgentAccessibilityService` extending `AccessibilityService`
- Implement screen reading by traversing the full `AccessibilityNodeInfo` tree
- Serialize the UI tree to a compact JSON structure:
  { "nodes": [{ "id": "...", "text": "...", "class": "...", "bounds": [x,y,w,h], "clickable": true }] }
- Implement `performGlobalAction()` for HOME, BACK, RECENTS, NOTIFICATIONS
- Implement `dispatchGesture()` for tap, long-press, swipe, and scroll
- Implement smart targeting: `findNodeByText()`, `findNodeById()`, `findNodeByClass()`
- When the accessibility tree is empty (blocked app), automatically trigger `vision_read_screen()`
  as a fallback, using the VLM to interpret the screenshot instead

---

### 4. Vision-Language Model (VLM) Integration

Implement full VLM-powered screen understanding as a production feature (not just a fallback):

- `VLMClient` sends a base64 PNG screenshot to a local VLM server (LLaVA/MiniCPM-V via Ollama)
- System prompt for VLM:
  "You are a UI analysis assistant. Given a screenshot, return ONLY a JSON object listing all
  visible interactive elements with their text, type (button/input/text/image), and bounding
  box as [x, y, width, height] in screen pixels. Format:
  { "elements": [{ "text": "...", "type": "button", "bounds": [x,y,w,h] }] }"
- `vision_read_screen()` tool: takes screenshot → calls VLMClient → returns element list to LLM
- `VisionGrounder`: converts VLM element descriptions into concrete (x,y) tap coordinates
- The agent can explicitly choose between `read_screen()` (fast, text-based) and
  `vision_read_screen()` (slower, visual) depending on the app context

---

### 5. Agent Loop (ReAct Engine)

Implement the core Observe-Think-Act reasoning loop in `AgentEngine`:

LOOP:
  1. Recall memory: search MemoryRepository for similar past tasks → inject as few-shot examples
  2. Capture current screen state → read_screen() or vision_read_screen()
  3. Build prompt:
       [System Prompt with tool definitions + plugin tools]
       [Task: user's original goal]
       [Memory: relevant past task examples from MemoryRepository]
       [History: previous thought/action/observation pairs]
       [Current Screen State]
  4. Query local LLM (via active LLMClient) → stream Thought + Action (tool call JSON)
  5. Parse and validate tool call JSON
  6. Execute tool via ToolRegistry (built-in or plugin)
  7. Capture result / observation
  8. Append step to history and persist to MemoryRepository
  9. If action == done() → save task run, break
  10. Repeat (max steps enforced)

---

### 6. Multi-Agent Coordination (Planner + Executor)

Implement a two-tier agent architecture for complex, multi-step tasks:

| Agent              | Class               | Role                                                                 |
|--------------------|---------------------|----------------------------------------------------------------------|
| Planner Agent      | `PlannerEngine`     | Receives high-level goal → produces ordered subtask graph (JSON)     |
| Executor Agent     | `ExecutorEngine`    | Runs the ReAct loop on one focused subtask → reports result back     |
| Orchestrator       | `AgentOrchestrator` | Feeds planner output to executor instances, handles sequencing       |

PlannerEngine system prompt:
  "You are a task planning agent. Given a high-level user goal, decompose it into a
  list of atomic, sequential subtasks that an executor agent can perform one at a time.
  Respond ONLY as JSON: { "subtasks": ["step 1...", "step 2...", ...] }"

- `AgentOrchestrator` runs subtasks sequentially by default; subtasks marked
  "parallel": true in the plan are dispatched concurrently via coroutines
- The UI shows a collapsible task plan panel with real-time subtask status badges:
  Pending → Running → Done / Failed
- On subtask failure, the Orchestrator re-invokes the Planner with failure context
  to replan the remaining steps

---

### 7. Task Memory & Learning from Past Executions

Build a persistent memory system so the agent improves over time:

TaskRun data model:
  id: UUID
  task: String
  steps: List<AgentStep>
  outcome: Outcome (SUCCESS / FAILURE / PARTIAL)
  userRating: Int? (1–5 stars)
  durationMs: Long
  timestamp: Instant

- `MemoryRepository` backed by encrypted Room DB (SQLCipher)
- `MemorySearchEngine`: keyword + optional vector embedding similarity search
  over past TaskRuns to find relevant examples for the current task
- `PromptBuilder` injects top-3 relevant past TaskRuns as few-shot examples:
  [MEMORY] Similar past task: "Set alarm for 7am" → opened Clock app → tapped Add →
  set time → tapped Save → SUCCESS
- Task History screen: list all past runs, filter by outcome, tap to replay, swipe to delete
- User feedback widget after each task: thumbs up/down and optional 1–5 star rating
- Long-term option: export successful task traces as LoRA fine-tuning dataset (JSONL)

---

### 8. Encrypted Task Logs & Privacy Controls

All sensitive data must be encrypted and privacy-respecting.

Storage encryption:
- Encrypt the Room database with SQLCipher using a device-bound key from Android Keystore
- Encrypt all stored screenshots with AES-256-GCM before writing to disk
- Store the AES key wrapped in an RSA key from Android Keystore (never leaves secure hardware)

PII Redaction:
- Before any task run is persisted, run it through `PIIRedactor`:
  - Regex patterns: phone numbers, email addresses, credit card numbers
  - On-device NER model (e.g., tiny BERT NER) for names and locations
  - Replaces detected PII with [REDACTED_PHONE], [REDACTED_NAME], etc.

Privacy Settings screen:
- Per-task-type logging toggle (e.g., "never log SMS / Call tasks")
- Auto-delete logs after N days (configurable: 7 / 30 / 90 / never)
- Export encrypted backup as .lagent file (AES-256 encrypted ZIP)
- Import backup from file
- "Stealth Mode" toggle: disables ALL logging, screenshot storage, and in-memory history
  for the duration of the session; agent still runs but leaves no trace
- First-launch privacy onboarding screen explaining exactly what is/is not stored

---

### 9. System Prompt Template

Use the following system prompt for the main executor LLM:

You are an Android device automation agent. Your goal is to complete user tasks
by controlling the Android device through a set of tools.

At each step you will receive:
- The user's task
- Relevant memory from past similar tasks (few-shot examples)
- The current screen content (accessibility tree OR visual element list)
- Your action history so far

You must respond ONLY in this JSON format:
{
  "thought": "Your reasoning about what to do next",
  "action": {
    "tool": "<tool_name>",
    "args": { ...tool arguments... }
  }
}

Available tools: [tap, swipe, type_text, press_key, open_app, read_screen,
vision_read_screen, take_screenshot, send_sms, make_call, set_alarm, search_web,
get_contacts, read_notifications, recall_memory, done, <plugin_tools...>]

Rules:
- Always read_screen() or vision_read_screen() before acting if unsure of current state
- Prefer read_screen() for speed; use vision_read_screen() if tree is empty or blocked
- Use recall_memory() at the start of complex tasks to check if a strategy is known
- Use done() when the task is fully completed
- If stuck after 3 retries on the same step, call done() with an error explanation
- Never guess coordinates; derive them from read_screen() or vision_read_screen() output

---

### 10. UI (Jetpack Compose)

Build a comprehensive multi-screen interface:

Main Chat Screen:
- Text input + mic button (SpeechRecognizer API for voice input)
- Scrollable chat thread with:
  - User task bubbles
  - Agent thought bubbles (collapsible)
  - Subtask plan panel (expandable, real-time status per subtask)
  - Action badges (e.g., "VLM Screen Read", "Plugin: Spotify", "Memory Hit")
  - Screenshot thumbnails for vision steps (tap to enlarge)
  - Observation / result previews
  - Final answer bubble with thumbs up/down + star rating widget
- Live status indicator: Thinking / Planning / Acting / Done / Failed

Settings Screen:
- LLM backend selector: Ollama / llama.cpp / On-Device MLC / MediaPipe Gemma
- LLM server URL field (default: http://10.0.2.2:11434)
- Model name / GGUF file selector
- VLM server URL + model (for vision_read_screen)
- Max steps / timeout / retry controls
- Planner mode toggle (single agent vs. Planner + Executor)
- Accessibility Service status + quick enable button
- Permission status dashboard (green/yellow/red per permission)

Model Manager Screen:
- Download GGUF models from HuggingFace URLs
- View model size, quantization, and benchmarked token/s
- Switch active model, delete downloaded models

Plugin Store Screen:
- Browse available tool packs
- Install / enable / disable / uninstall plugin tools
- View each plugin's tool list and required permissions

Task History Screen:
- List all past TaskRuns with task text, outcome badge, rating, and timestamp
- Filter by outcome (Success / Failure / All)
- Tap a run to view full step-by-step replay
- Swipe to delete individual runs
- "Clear all history" button

Privacy Settings Screen:
- Per-task-type logging toggles
- Auto-delete interval selector
- Stealth Mode toggle
- Export / Import encrypted backup
- Privacy notice and data summary

---

### 11. Permissions & Manifest

Request and handle the following permissions:
- BIND_ACCESSIBILITY_SERVICE
- SYSTEM_ALERT_WINDOW (for overlay indicators)
- READ_CONTACTS, WRITE_CONTACTS
- SEND_SMS, READ_SMS
- CALL_PHONE
- SET_ALARM
- INTERNET
- RECORD_AUDIO (for voice input)
- FOREGROUND_SERVICE (to keep agent running in background)
- POST_NOTIFICATIONS (Android 13+)
- READ_EXTERNAL_STORAGE / MANAGE_EXTERNAL_STORAGE (for GGUF model files)
- USE_BIOMETRIC (for unlocking encrypted backup export)

---

## FOLDER STRUCTURE

LocalAgent/
├── app/src/main/
│   ├── java/com/localagent/
│   │   ├── agent/
│   │   │   ├── AgentEngine.kt              # Core single-agent ReAct loop
│   │   │   ├── AgentState.kt               # State sealed class
│   │   │   ├── AgentStep.kt                # Thought/Action/Observation data
│   │   │   ├── ToolExecutor.kt             # Dispatches tool calls
│   │   │   ├── PromptBuilder.kt            # Builds prompt (memory + screen + history)
│   │   │   └── ResponseParser.kt           # Parses JSON tool-calls from LLM
│   │   ├── multiagent/
│   │   │   ├── AgentOrchestrator.kt        # Coordinates Planner + Executors
│   │   │   ├── PlannerEngine.kt            # Decomposes goal into subtask graph
│   │   │   ├── ExecutorEngine.kt           # Runs ReAct loop on a single subtask
│   │   │   └── SubtaskGraph.kt             # Data model for task plan
│   │   ├── tools/
│   │   │   ├── ToolRegistry.kt             # Merges built-in + plugin tools
│   │   │   ├── AccessibilityTools.kt       # tap, swipe, type, read_screen
│   │   │   ├── VisionTools.kt              # vision_read_screen, take_screenshot
│   │   │   ├── SystemTools.kt              # open_app, set_alarm, call, sms
│   │   │   ├── WebTools.kt                 # search_web
│   │   │   ├── MemoryTools.kt              # recall_memory
│   │   │   └── AgentTool.kt                # Plugin tool interface
│   │   ├── plugins/
│   │   │   ├── PluginRegistry.kt           # Discovers and loads plugin tools
│   │   │   ├── PluginLoader.kt             # APK + JSON manifest plugin loading
│   │   │   └── builtin/
│   │   │       ├── HomeAssistantTools.kt
│   │   │       ├── SpotifyTools.kt
│   │   │       ├── FileManagerTools.kt
│   │   │       └── ShellCommandTools.kt
│   │   ├── llm/
│   │   │   ├── LLMClient.kt                # Unified interface
│   │   │   ├── OllamaClient.kt
│   │   │   ├── LlamaCppClient.kt
│   │   │   ├── OnDeviceMLCClient.kt        # MLC LLM JNI
│   │   │   ├── OnDeviceLlamaCppClient.kt   # llama.cpp JNI
│   │   │   ├── MediaPipeGemmaClient.kt
│   │   │   ├── VLMClient.kt                # Vision-Language Model client
│   │   │   ├── LLMClientFactory.kt
│   │   │   └── ModelManager.kt             # Download/switch/delete GGUF models
│   │   ├── accessibility/
│   │   │   ├── AgentAccessibilityService.kt
│   │   │   ├── ScreenReader.kt             # Parses AccessibilityNodeInfo → JSON
│   │   │   └── VisionGrounder.kt           # Maps VLM element descriptions to coords
│   │   ├── memory/
│   │   │   ├── MemoryRepository.kt
│   │   │   ├── MemorySearchEngine.kt       # Keyword + vector similarity search
│   │   │   ├── TaskRun.kt
│   │   │   └── AgentStep.kt
│   │   ├── privacy/
│   │   │   ├── PIIRedactor.kt              # Regex + on-device NER PII redaction
│   │   │   ├── EncryptionManager.kt        # AES-256-GCM + Android Keystore
│   │   │   ├── BackupManager.kt            # Export/import encrypted .lagent backup
│   │   │   └── StealthMode.kt              # Disables all logging for session
│   │   ├── ui/
│   │   │   ├── MainActivity.kt
│   │   │   ├── NavGraph.kt
│   │   │   ├── chat/
│   │   │   │   ├── ChatScreen.kt
│   │   │   │   ├── ChatViewModel.kt
│   │   │   │   └── components/
│   │   │   ├── settings/
│   │   │   │   ├── SettingsScreen.kt
│   │   │   │   └── PrivacySettingsScreen.kt
│   │   │   ├── models/
│   │   │   │   └── ModelManagerScreen.kt
│   │   │   ├── plugins/
│   │   │   │   └── PluginStoreScreen.kt
│   │   │   └── history/
│   │   │       └── TaskHistoryScreen.kt
│   │   ├── di/
│   │   │   └── AppModule.kt
│   │   └── data/
│   │       ├── db/
│   │       │   ├── AgentDatabase.kt        # SQLCipher-encrypted Room DB
│   │       │   └── dao/
│   │       └── repository/
│   │           └── AgentRepository.kt
│   └── res/
│       └── xml/
│           └── accessibility_service_config.xml

---

## DELIVERABLES

1. Fully buildable Android Studio project (Gradle 8.x, AGP 8.x)
2. All Kotlin source files with KDoc comments
3. `AndroidManifest.xml` with all required permissions and service declarations
4. `accessibility_service_config.xml` configured for full screen interaction
5. Room DB schema with SQLCipher integration
6. Working `VLMClient` with fallback logic wired into `AgentAccessibilityService`
7. Working `PlannerEngine` + `ExecutorEngine` + `AgentOrchestrator`
8. Working `MemoryRepository` with keyword search and few-shot injection
9. Working `PIIRedactor` with regex + placeholder NER integration point
10. Working `EncryptionManager` using Android Keystore + AES-256-GCM
11. Working `PluginRegistry` with at least one example plugin (HomeAssistant or Spotify)
12. Unit tests for: `PromptBuilder`, `ResponseParser`, `AgentEngine`, `PIIRedactor`, `PlannerEngine`
13. `README.md` covering setup, plugin SDK guide, example tasks, and privacy architecture

---

## EXAMPLE TASKS THE AGENT SHOULD HANDLE

Basic (single-agent, accessibility tree):
- "Set an alarm for 7 AM tomorrow"
- "Call Mom"
- "Read my last 3 notifications"
- "Open Settings and enable Dark Mode"

Intermediate (multi-step tool chaining):
- "Send a WhatsApp message to John saying I'll be late"
- "Open YouTube and search for lo-fi music"
- "Take a screenshot and share it via Gmail"

Vision-required (VLM fallback for blocked apps):
- "Open my banking app and check my account balance"
- "Open Google Maps and navigate to the nearest coffee shop"

Multi-agent (Planner + Executor):
- "Book a restaurant for Saturday: find top-rated Italian places, open the website,
   and locate the reservation link"
- "Turn on WiFi, connect to HomeNetwork, open Chrome, go to reading list, read first article"

Memory-assisted:
- "Do the same alarm thing you did last time"
- "Set my morning routine" (agent recalls a multi-step routine from memory)

Plugin-powered:
- "Turn off the living room lights" (HomeAssistant plugin)
- "Play my Chill Vibes playlist on Spotify" (Spotify plugin)
```

---

## 🔧 Local LLM + VLM Setup Guide

### Option A — Ollama (Recommended for text LLM + VLM)
```bash
# Install Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Pull a text LLM for the main agent
ollama pull llama3

# Pull a Vision-Language Model for vision_read_screen()
ollama pull llava

# Start the server accessible to emulator (10.0.2.2) or real device (LAN IP)
OLLAMA_HOST=0.0.0.0 ollama serve
```

### Option B — llama.cpp Server
```bash
git clone https://github.com/ggerganov/llama.cpp
cd llama.cpp && make

# Text LLM
./llama-server -m llama-3-8b-instruct.gguf --host 0.0.0.0 --port 8080

# VLM (LLaVA) — run on a second port
./llama-server -m llava-v1.6.gguf --mmproj mmproj-model-f16.gguf --host 0.0.0.0 --port 8081
```

### Option C — On-Device Inference (MLC LLM)
```bash
# Install MLC LLM Python package
pip install mlc-llm

# Compile Llama 3 for Android (Vulkan target)
mlc_llm compile llama-3-8b-instruct --device android --quantization q4f16_1 -o dist/

# Bundle the compiled .so and model weights into the app's assets/
# OnDeviceMLCClient JNI bindings load these at runtime — no server needed
```

> **Tip for real devices:** Use your dev machine's LAN IP (e.g., `192.168.1.x`)
> instead of `10.0.2.2`, and make sure both devices are on the same Wi-Fi network.

---

## ⚠️ Important Considerations

- **Accessibility Service** must be manually enabled by the user via Android Settings > Accessibility.
- **Sensitive permissions** (CALL_PHONE, SEND_SMS) require explicit runtime grants.
- **Security:** LLM/VLM servers must never be exposed to the internet — use localhost or LAN only.
- **Model choice matters:** Use instruction-tuned models with strong JSON output reliability
  (Llama 3 Instruct, Mistral Instruct, Phi-3 Mini Instruct). Poor JSON compliance breaks parsing.
- **VLM latency:** `vision_read_screen()` is significantly slower than `read_screen()`.
  Use it selectively when the accessibility tree is empty or an app blocks it.
- **On-device inference** requires at least 6 GB RAM for 7B-class models.
  Prefer Phi-3 Mini or Gemma 2B on lower-end devices.
- **Plugin security:** Validate all plugin tool schemas before loading. Sandboxed execution
  should be considered for untrusted third-party plugins.
- **Privacy first:** Always show a clear onboarding screen on first launch. Stealth Mode
  and per-type logging toggles give users full control over what is stored.
