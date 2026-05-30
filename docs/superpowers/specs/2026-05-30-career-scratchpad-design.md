# Career Scratchpad Design

## Purpose

Career Scratchpad is a native Android interview lab for fast, durable one-off work. It replaces unreliable Kotlin scratch files and scattered tiny repos with one app that can hold algorithm puzzles, Compose widget sketches, API design exercises, parser demos, and small architecture samples.

The app is meant to be useful during interview prep and credible during live discussion: open the app, show the active lab, adjust inputs, run the solution, and explain the implementation and tradeoffs.

## Product Direction

The first version is an active lab, not a portfolio landing page. It opens directly into the current exercise and keeps older labs available through a compact switcher.

Each lab is normal Kotlin and Compose code checked into the app. The app will not try to dynamically compile Kotlin snippets on-device. That avoids recreating Android Studio Scratch instability inside the product.

## MVP Scope

The first lab is `Secret Message Decoder`, based on the current coding exercise:

- Accept pasted document/table text as the primary MVP input.
- Accept a public Google Doc URL as best-effort input only when it can be fetched without authentication.
- Parse rows containing `x`, character, and `y` data.
- Render the output grid in a monospace preview.
- Provide orientation toggles: flip Y, flip X, and swap axes.
- Show parse stats and basic errors.
- Include a source/approach panel with the Kotlin implementation summary and interview notes.

Out of scope for the first version:

- Dynamic Kotlin compilation in the app.
- Multi-language execution.
- Cloud accounts or saved remote sync.
- Full portfolio polish.
- Automated Google Docs auth flows.
- Saving or reproducing live assessment answers. Assessment-inspired labs should preserve prompts, reasoning patterns, and tooling needs, not completed response text.

## Assessment-Inspired Lab Backlog

The current coding assessment suggests several lab and sub-lab types that Career Scratchpad should support after the first decoder lab. These are not separate apps; they are future entries in the same active lab shell.

### Code Analysis Labs

These labs support interview questions where the task is to compare snippets or judge implementation quality:

- **Palindrome Best Practices**: compare three pseudocode implementations, identify the most maintainable/general solution, and write a short explanation.
- **Prime Complexity Comparison**: compare valid and invalid prime-checking approaches, surface time and space complexity, and explain why a square-root bound is better than full-range checks or extra factor storage.
- **Complexity Notes Panel**: reusable panel for Big-O, correctness, edge cases, and maintainability comments.

### Algorithm Ordering Labs

These labs support drag/order style problems where the answer is a sequence of steps:

- **Digit Frequency With Arithmetic**: arrange the steps for extracting digits from an integer with `mod 10`, updating a frequency map, and reducing the number with integer division.
- **Step Sequencer Component**: reusable UI for moving pseudocode steps up/down and previewing the assembled algorithm.

### Response Evaluation Labs

These labs support side-by-side AI response comparisons:

- **Safety Refusal Comparison**: compare a safe refusal against a response that gives harmful operational instructions.
- **Helpfulness Rubric Panel**: reusable rubric for correctness, safety, instruction-following, clarity, and whether a response should refuse.
- **Explanation Builder**: space to draft concise 2-3 sentence reasoning without storing live assessment answers.

### Coding Challenge Labs

These labs support full coding tasks:

- **Secret Message Decoder**: first implementation target; parse published Google Doc grid data and render the uppercase message.
- **Parser Harness**: reusable input/output runner for document, table, JSON, CSV, or copied-web-text parsing problems.
- **Orientation/Visualization Controls**: reusable toggles for grid problems, including axis swap and flips.

### Career Profile Prep Labs

These are not coding exercises, but the app can later help maintain reusable source material for applications:

- **Experience Inventory**: structured notes for professional background, roles, projects, and technologies.
- **Skills Matrix**: reusable list of languages, frameworks, tooling, testing, cloud, and data skills.
- **Application Notes**: place to draft reusable, user-authored summaries for profile questions.

These profile labs must remain user-controlled writing aids. They should not auto-submit, impersonate, or overwrite external application forms.

## App Model

The app has a small lab registry:

- `Lab`: metadata for each exercise, including id, title, category, description, and screen entrypoint.
- `LabRegistry`: ordered list of available labs and the default active lab.
- Each lab owns its own models, parser/solver code, and Compose screen.

Initial categories:

- `Algorithm`
- `Compose UI`
- `API Design`
- `Architecture`
- `Data Parsing`
- `Code Analysis`
- `Response Evaluation`
- `Career Prep`

The first release only needs the `Secret Message Decoder` lab, but the registry should make adding the next lab straightforward.

## User Experience

Launch opens directly to the active lab. The layout should work on emulator, phone, and desktop-sized preview windows:

- Top app bar: app title, current lab title, lab switcher.
- Input section: URL field and paste area.
- Controls section: decode action and orientation toggles.
- Output section: monospace grid preview with copy affordance.
- Details section: tabs or segmented controls for `Result`, `Source`, and `Notes`.

The UI should feel like a practical developer tool: compact, legible, calm, and fast. It should prioritize showing the working solution over explanation text.

## Secret Message Decoder Logic

The decoder should be testable without Compose:

- `SecretMessageCell(x: Int, y: Int, value: String)`
- `SecretMessageParseResult(cells, warnings)`
- `SecretMessageRenderOptions(flipY, flipX, swapXY, blankChars)`
- `SecretMessageDecoder.parse(text: String)`
- `SecretMessageDecoder.render(cells, options): String`

Parsing should support at least:

- Whitespace rows, for example `0 █ 0`.
- Copied table-like text where values appear as repeating `x`, char, `y` triplets.

The renderer computes grid bounds from parsed cells, fills unspecified positions with spaces, and emits newline-separated rows.

## Error Handling

The lab should avoid silent failure:

- Empty input: show a short message asking for pasted document data or a URL.
- No parsed cells: show parse guidance and preserve the input.
- Invalid coordinates: collect warnings instead of crashing.
- Oversized output: render a bounded preview and expose counts.
- Network fetch failure: show the error and keep the paste fallback as the supported path.

## Technical Stack

Use a standard native Android project:

- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- JVM 17
- Unit tests for decoder logic
- Compose preview and optional instrumented test for the first lab screen

Prefer ordinary Android Studio run/debug workflows over Scratch files. The app should run on emulator and real devices.

## Project Structure

Target structure:

```text
career-scratchpad/
  app/
    src/main/java/com/monroestone/careerscratchpad/
      MainActivity.kt
      lab/
        Lab.kt
        LabRegistry.kt
      labs/secretmessage/
        SecretMessageLabScreen.kt
        SecretMessageDecoder.kt
        SecretMessageModels.kt
      ui/
        CareerScratchpadTheme.kt
    src/test/java/com/monroestone/careerscratchpad/
      labs/secretmessage/
        SecretMessageDecoderTest.kt
  docs/superpowers/specs/
```

## Testing And Verification

First implementation should verify:

- Decoder parses the simplified `F` example.
- Renderer produces the expected orientation with default options.
- Flip/swap options change output predictably.
- Empty and malformed input return useful errors or warnings.
- App builds and launches on the emulator or a connected Android device.

## Future Direction

After the first lab works, add a lightweight lab creation workflow:

- Template package for new labs.
- Registry entry pattern.
- Optional docs page for each lab.
- A reusable `LabScaffold` Compose component for input, output, source, and notes.

Later labs can include API design prompts, Compose widget sketches, state-machine demos, and small data-structure exercises.
