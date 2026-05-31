# Android Development Workflow

This project is a native Android/Compose interview lab. The workflow keeps Android Studio in charge of interactive IDE work while Codex handles focused code edits, repo inspection, narrow verification, and documentation.

## Responsibilities

### Android Studio

Use Android Studio for:

- Gradle sync and IDE project import.
- Emulator and physical-device selection.
- Interactive run/debug sessions.
- Compose previews when visual inspection is faster in the IDE.
- SDK, JDK, and Android Gradle Plugin prompts that need local UI decisions.

### Codex

Use Codex for:

- Reading specs, repo state, and relevant workflow docs before edits.
- Editing Kotlin, Compose, tests, Gradle files, and docs.
- Keeping implementation aligned with `docs/superpowers/specs/2026-05-30-career-scratchpad-design.md`.
- Adding or updating Compose previews for every new or meaningfully changed screen/component.
- Writing pure Kotlin tests before parser/solver implementation when practical.
- Running narrow Gradle verification only with log capture.
- Summarizing targeted failures instead of streaming full logs.

## Gradle Runs

Gradle output should not be streamed through chat. When Codex runs Gradle, use a log file:

```bash
mkdir -p build/codex-logs
./gradlew :app:testDebugUnitTest --console=plain > build/codex-logs/testDebugUnitTest.log 2>&1
```

Inspect only focused excerpts:

```bash
rg -n "FAILED|FAILURE|Exception|error:|warning:" build/codex-logs/testDebugUnitTest.log
tail -40 build/codex-logs/testDebugUnitTest.log
```

Preferred Gradle tasks:

- `./gradlew :app:testDebugUnitTest --console=plain`
- `./gradlew :app:assembleDebug --console=plain`

Avoid unless explicitly approved:

- `clean`
- full multi-module builds when a narrow task is enough
- dependency refreshes
- release packaging
- long log streaming

If a Gradle run fails because dependencies or metadata require network access, stop and request approval through the sandbox escalation flow. Do not silently broaden the task.

## Repo Hygiene

Before committing Android scaffold or workflow changes, inspect:

```bash
git status --short
```

Default commit policy:

- Commit source, tests, docs, Gradle wrapper files, and shared Gradle config.
- Do not commit `local.properties`.
- Do not commit `.gradle/` or `build/`.
- Decide deliberately whether `.idea/` files are shared project config or local IDE state before committing them.

Add or update `.gitignore` before staging scaffold work.

## Implementation Order

For the first lab, follow this order:

1. Align repo hygiene and project metadata.
2. Confirm the active spec.
3. Write failing unit tests for pure Kotlin logic.
4. Implement the smallest parser/renderer that passes the tests.
5. Wire the logic into Compose.
6. Add or update Compose previews for the changed screen and key UI states.
7. Add focused UI affordances only after logic is verified.
8. Run log-captured unit tests only when explicitly asked.
9. Let Android Studio own interactive sync/run/debug unless Codex is explicitly asked to run a narrow command.

## Secret Message Decoder Target

The first lab should implement the spec, not a generic cipher demo:

- `SecretMessageCell(x: Int, y: Int, value: String)`
- `SecretMessageParseResult(cells, warnings)`
- `SecretMessageRenderOptions(flipY, flipX, swapXY, blankChars)`
- `SecretMessageDecoder.parse(text: String)`
- `SecretMessageDecoder.render(cells, options): String`

The parser should support pasted table/document text first. Public Google Doc URL fetching is best-effort and should not block the MVP.

## Skill Promotion Gate

Create a personal Codex skill only after this workflow works in practice for at least the first full lab pass.

Candidate skill path:

```text
/Users/monroe/.codex/skills/android-compose-lab-workflow/SKILL.md
```

The future skill should distill this doc into a concise reusable workflow covering:

- Android Studio and Codex responsibility split.
- Log-captured Gradle verification.
- Repo hygiene for Android projects.
- TDD for pure Kotlin lab logic.
- When to use Android emulator QA tooling.
- Verification gates before claiming success.
