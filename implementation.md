# Implementation Plan: Rebuild writeAI (TypeScript) → Kotlin + CodeAssist

## Source
- Repo: https://github.com/Greenlionzz/writeAI
- Stack: React, TypeScript, Vite, Tailwind CSS, Capacitor (Android wrapper)
- Features: multi-project manuscript workspace, plot/character/location boards, scene editor (Markdown-style), progress tracking, AI actions (Gemini), PDF/EPUB/ODT export, keyboard shortcuts, mobile responsive layout.

## Target
- Kotlin native (CodeAssist build — no Gradle)
- CodeAssist uses its own incremental task engine (TaskGraph, fingerprint up-to-date checks, persistent cache) and drives aapt2/D8/apksigner directly on device.
- Build files: CodeAssist `workspace.json` + per-module `module.toml` / native model; NOT `build.gradle`.
- UI: Compose Multiplatform (CodeAssist hosts Compose UI) or native Android Compose; project model mapped to CodeAssist's `ProjectImporter` / `BuildSystem` SPI.

## Key CodeAssist constraints (from docs/build-system.md & README.md)
- No Gradle daemon; build is a DAG over module graph with typed inputs/outputs.
- Android pipeline tasks: mergeResources → aapt2Link (+R) → [compileKotlin → compileJava] → dexBuilder → packageApk → sign.
- Kotlin/Java interop: Kotlin emits to sibling classes; Java feeds Kotlin `.java` for resolution; both join compile classpath.
- Compose compiler plugin is required for any `@Composable`; applied automatically when `androidx.compose.runtime.Composable` is on classpath.
- Library/AAR resources contribute to merged R; library dex is non-final, app generates final R.
- Native build does not execute arbitrary Gradle scripts; uses `GradleImport` for compatibility mode only (tolerant non-evaluating reader). Native projects declare model directly.

## Build verification: CodeAssist CAN build APKs (confirmed)
- **Yes, CodeAssist builds APKs directly on device without Gradle.** It uses its own incremental task engine and drives aapt2/D8/apksigner directly.
- **Build files**: `.platform/workspace.json` (project-level) + per-module `module.toml` (module-level). No `build.gradle` required.
- **Build pipeline**: resolve → aapt2 → javac/K2 → D8 → apksigner (all in-process except aapt2 as subprocess).
- **Build commands** (headless CLI `codeassist`):
  - `codeassist assemble --project . --variant debug` — builds APK
  - `codeassist tasks` — lists buildable tasks
  - `codeassist create --template android-app --name MyApp --package com.example.myapp --project ./MyApp` — scaffold new project
- **Android SDK**: located via `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `local.properties`, or default paths. Needs build-tools and a platform (e.g., API 36).
- **CI/CD**: GitHub Action `tyron12233/CodeAssist@v1` builds APK in CI without Gradle. Inputs: `variant`, `module`, `task`, `java-version`, `android-api`, `build-tools`.
- **`.platform/` is version-controlled** (like `build.gradle`); `.platform/caches/` is gitignored.
- **Desktop build**: `./gradlew :build-cli:installDist` produces the `codeassist` launcher binary (~220 MB unpacked).

## Implementation steps (ordered)

1. **Project scaffolding (CodeAssist model)**
   - Create `workspace.json` declaring root module type `android-app` (namespace, SDK, build types/flavors).
   - Declare Kotlin source sets (`src/main/kotlin`, `src/main/res`) and Compose dependency.
   - Register in CodeAssist as native project (`.platform/external-project` not needed; native model is truth).

2. **Data/model layer in Kotlin**
   - Convert TypeScript project types (`Project`, `Scene`, `Chapter`, `Character`, `Location`, `PlotBeat`) to Kotlin data classes.
   - Storage: CodeAssist uses on-device persistence; mirror local-storage logic with Kotlin serialisation (JSON/Kotlinx Serialization) to disk instead of browser `localStorage`.
   - Implement `ProjectService`-style manager for CRUD on projects/manuscript outline.

3. **UI layer — Compose Multiplatform / Android Compose**
   - Rebuild Responsive layout: file/editor/reference split for tablets; mobile navigation drawer.
   - Scene editor: rich text with Markdown-style formatting controls (headings, emphasis, lists, scene breaks) mapped to Compose `TextField` / `RichTextEditor` backed by Kotlin text model.
   - Planning boards (plot, character bible, locations): Composable list/grid views with drag-reorder, tags, status.
   - Theme: dark / light / sepia; font family / size settings stored in preferences.

4. **Manuscript / outline engine**
   - Hierarchy: Project → Book → Act → Chapter → Scene.
   - Scene status tracking, synopsis, POV character, location links, notes, target word count, actual word count.
   - Reorder, rename, delete nodes; propagate word-count progress up hierarchy.

5. **AI integration (Gemini)**
   - Replicate Express endpoints (`/api/ai/test-key`, `/api/ai/action`, `/api/ai/outline`, etc.) as Kotlin service or direct SDK calls within app.
   - Use Google GenAI SDK (pure-Java, runs in process on ART) for Gemini requests.
   - Actions: proofread, tighten prose, tone adjust, continue/expand scene, summarize, dialogue, plot-hole check, world-build, title suggestion, custom instruction.
   - Apply output: insert or replace prose in editor model; trigger text change events.

6. **Editing & analysis tools**
   - Undo/redo stack over document model.
   - Find/replace scoped to current scene.
   - Snapshots/revisions: save scene state versions; restore previous version; add revision comments to selected text range.
   - Style critique / readability inspector: implement as Kotlin analyzers over text segments (wordiness, passive voice, weak verbs, clichés, readability scores); map suggestions to Compose UI overlays.
   - Smart paragraphing: algorithm to suggest breaks based on sentence length / dialogue ratio; compare proposed vs original.

7. **Progress tracking & analytics**
   - Daily writing log, word-count targets, streaks, velocity, projected completion.
   - Activity heatmap (Compose Canvas or chart library), plot timeline (D3-like in Compose), core cast list.

8. **Export**
   - PDF: use Kotlin PDF generation library (e.g., OpenPDF or Compose-to-PDF) with options: paper size, font, title page, TOC, synopsis, page numbers, running headers.
   - EPUB / ODT: generate from manuscript model via Kotlin libraries.
   - JSON backup: serialize full `Project` model.

9. **Keyboard shortcuts & mobile**
   - Map `Ctrl/Cmd+F` (focus mode), `Ctrl/Cmd+P` (global search), `Ctrl/Cmd+K` (AI panel), `Ctrl/Cmd+\` (split reference), `Ctrl/Cmd+S` (save status) to Compose key handlers.
   - Shortcuts reference screen.

10. **Build & package with CodeAssist**
    - Configure build request (`BuildRequest`) for debug/release variant.
    - Run `compileKotlin` (K2 compiler with Compose plugin when needed) → `compileJava` → dex → package → sign.
    - Build APK on device using native Android pipeline (no Gradle wrapper needed).
    - If exporting to Gradle for Android Studio/CI, use `GradleProjectExport` (renders model to `settings.gradle.kts` + `build.gradle.kts` per module, zips with sources).

## Skills / docs used
- `docs/build-system.md`: native incremental task engine, Android pipeline tasks, Kotlin/Java interop, Compose plugin, Gradle compatibility layer.
- `README.md`: CodeAssist overview (no Gradle daemon, on-device build, Compose Multiplatform UI, module layout, build commands).
- No additional external skill needed; build logic covered by CodeAssist docs.

## Notes
- Because CodeAssist does not host Gradle scripts natively, avoid any logic that requires `build.gradle` execution (e.g., dynamic dependency version substitution from properties). Declare static coordinates in model / `workspace.json`.
- AI key/config: store user-entered Gemini API key in CodeAssist settings registry; fall back to server-side `GEMINI_API_KEY` if configured (same `.env` pattern, adapted to Kotlin config file).
- Project backup/export can reuse the `GradleProjectExport` mechanism when sharing to Android Studio; for pure CodeAssist distribution, APK is built directly.
