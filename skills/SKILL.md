---
name: appdimens-kmp-workflow
description: Use this skill for any Kotlin Multiplatform responsive layout or scaling question — including making apps look right on tablets, foldables, desktop windows, iOS, macOS, or the browser — and whenever the user mentions appdimens-kmp, sdp, hdp, wdp, ssp, DimenCache, autoResize, responsive dp/sp, or scaling strategies. Also trigger when the user wants to add the AppDimens KMP dependency, choose between Compose Multiplatform vs the non-Compose `code` API, implement constraint-based resize, or convert plain dp/sp values to scaled equivalents. When in doubt, use this skill — it covers the full lifecycle: install, strategy selection, Compose/code implementation, and resize, on every KMP target.
---

# AppDimens KMP — project workflow

**Library (1.0.1):** `appdimens-kmp` (scaled + core), `appdimens-kmp-<strategy>`, BOM `appdimens-kmp-bom`. Packages use the **`com.appdimens.kmp.*`** root (this is the Kotlin Multiplatform library — the Android-only predecessor used `com.appdimens.dynamic.*`; do not mix them). See [MODULES.md](../DOCUMENTATION/MODULES.md).

**Platforms:** Android (min SDK 24) · JVM desktop · iOS (`iosArm64`/`iosSimulatorArm64`) · macOS (`macosArm64`) · Web (Kotlin/JS + wasmJs) · Linux (`linuxX64`/`linuxArm64`) · Windows (`mingwX64`). One API for all of them (Linux/Windows expose the `code` API only).

**Install:** `platform("io.github.bodenberg:appdimens-kmp-bom:1.0.1")` then the modules you need. Kotlin imports are `com.appdimens.kmp.*`.

On release bumps, update version URLs in this file, `library-map.md`, and `reference.md` together.

**What's New in 1.0.0 (KMP port):**
- Full Kotlin Multiplatform port — same packages/API/math on Android, JVM, iOS, macOS and wasmJs
- Platform-neutral `AppDimensContext` window handle for non-Compose APIs (Android wraps `Context`; desktop wraps AWT; iOS/macOS wrap `UIScreen`/`NSScreen`; web wraps the viewport)
- Event-driven config watcher on Android; live-window snapshot rebuild on desktop/web/iOS/macOS (resize-aware on every platform)
- Specialized kernels (`resolveSdpPx`, `resolveSdpaPx`, etc.) — zero branches
- Non-Compose fast lane (`fastMetricsForCode`) — skips ThreadLocal
- Compose fast lane reads `metricsScope ?: LocalDimenMetrics` (one CompositionLocal + one multiply) — no `LocalContext`
- DimenMetrics eager AR computation — removes `synchronized` probe
- R8 pre-shrink for all 14 Android AARs (`optimization { minify = true }`)
- BenchLab KMP — competitor benchmark that runs on Android, JVM, iOS, macOS and the browser

**What's New in 1.0.1 (audit fixes + full target matrix):**
- `fastPartition` race fixed — metrics + partition published as one atomic `FastPartitionSlot` (was two independent atomics that could serve another window's metrics)
- Android context cache cycle fixed — `WeakHashMap<Context, WeakReference<AndroidAppDimensContext>>` (value no longer holds the key strongly)
- `WeakIdentityHashMap` structural defect fixed (JVM/Android) — strong `HashMap` of `IdentityWeakReference` wrappers + `ReferenceQueue` instead of `WeakHashMap<WeakKey<K>, V>` (the wrapper was the weak key and could be collected while the referent was alive; entries now live exactly as long as their referent)
- Native `MetricsScopeHolder` is `@ThreadLocal` — each Kotlin/Native worker gets its own metrics slot (previously one shared mutable global could cross snapshots between workers)
- Native/Web identity maps now compare with `===` — distinct-but-`equals()` window handles no longer collapse into one key (was `LinkedHashMap`)
- Configuration listeners lifecycle — listener is context-free (no value→key cycle), watcher is reference-counted (`acquireConfigWatcher` / `releaseConfigWatcher`), the Android `AppDimensProvider` pairs them via `DisposableEffect`, and the Android registry holds listeners weakly — a destroyed Activity is collectable even without explicit `dispose()`
- Strict race tests — `DimenCacheRaceTest` requires the exact expected value per key/snapshot; new GC / identity / lifecycle / native-worker tests
- New targets: classic Kotlin/JS (`js`, IR), `linuxX64`, `linuxArm64`, `mingwX64` (all modules, via the shared `appdimens.kmp-library` convention plugin)
- Encapsulated diagnostics — `DimenCache.isInitialized` is a plain `Boolean`, `cacheStats()` returns immutable `CacheStats`; experimental atomics are `internal`
- CI restored — `verify-linux` + `verify-apple` gates (Apple gate no longer references the nonexistent `linkDebugFrameworkIosSimulatorArm64` task); wrapper `distributionSha256Sum` pinned; Compose dev repo removed

**Authoritative docs (repo root, Git ref `1.0.1`):**
- [README.md](../README.md) — install, `AppDimensProvider`, `DimenCache` (snapshot-partitioned; event-driven config watcher; specialized kernels), platform table
- [DOCUMENTATION/MODULES.md](../DOCUMENTATION/MODULES.md) — Maven/Gradle graph (principal vs satellites) + target table
- [DOCUMENTATION/README.md](../DOCUMENTATION/README.md) — all strategies, decision flow
- [DOCUMENTATION/COMPOSE-API-CONVENTIONS.md](../DOCUMENTATION/COMPOSE-API-CONVENTIONS.md) — Compose naming, facilitators, Plain chains, `code` parity
- [DOCUMENTATION/resize.md](../DOCUMENTATION/resize.md) — `compose.resize` / `code.resize`
- [GUIDE-FOR-BEGINNERS.md](../GUIDE-FOR-BEGINNERS.md) — narrative walkthrough
- [PERFORMANCE.md](../PERFORMANCE.md) / [PERFORMANCE-COMPARATIVE.md](../PERFORMANCE-COMPARATIVE.md) — measured numbers per platform
- Examples: [`app`](../app/README.md) (KMP demo) · [`app-android`](../app-android) (Android-only demo)

**Package map and strategy → doc index:** [library-map.md](library-map.md)

---

## Agent Preflight (run before any non-trivial edit)

1. Read [library-map.md](library-map.md) for package layout and strategy ↔ doc mapping.
2. For the **specific packages/symbols** you'll touch: browse the local source tree under `library/src/commonMain/kotlin/com/appdimens/kmp/…` (principal) or the matching `library-<strategy>/src/commonMain/kotlin/com/appdimens/kmp/<strategy>/` satellite. Platform adapters live in `androidMain` / `jvmMain` / `iosMain` / `macosMain` / `wasmJsMain` (and shared `nativeMain`). For API detail, see [DOCUMENTATION/index.md](../DOCUMENTATION/index.md) and the strategy guides. Do not rely on memory.
3. Skim the example that matches the user's stack (`app/src/commonMain/kotlin/com/example/app/compose/…` for Compose Multiplatform, or the `app-android` module for Android-only). Restrict deep reading to **relevant packages and call sites** — full module audits only when explicitly requested.

**Hard rule:** never surface `ignoreMultiWindows`, `*i`, or `*ia` suffixes to users.

---

## Docs maintenance

When editing or auditing `DOCUMENTATION/`:

- Strategy guides live at `DOCUMENTATION/<strategy>.md` and are the authoritative per-strategy references — update them when API or packaging changes.
- **Package table** in `DOCUMENTATION/index.md` maps every `com.appdimens.kmp.code.<strategy>` / `compose.<strategy>` package to its Maven artifact and strategy doc — keep it in sync.
- **Terminology:** since 1.0.0 the cache is **snapshot-partitioned** (per immutable `DimenMetrics` window snapshot) and there is **no disk persistence**. Never write new prose about a "shard cache" or persistence.

---

## Phase 0 — Interactive Baseline

Ask questions **one at a time**. Wait for the answer before moving to the next step. Skip any already answered in the conversation.

### 0.1 UI Stack

Which surface is in scope?

| Choice | Package family | Key note |
|--------|---------------|----------|
| **Compose Multiplatform** | `com.appdimens.kmp.compose.*` | `16.sdp`, `scaledDp { }`; needs `AppDimensProvider` for facilitators. Same code on Android, desktop, iOS, macOS and web |
| **Non-Compose `code`** | `com.appdimens.kmp.code.*` | `DimenSdp.sdp(appContext, 16)`; takes an `AppDimensContext` window handle; outputs px |

Record: drives imports, `AppDimensProvider` need, and whether resize runs in `compose.resize` or `code.resize`. On Android the `code` API is used from Kotlin (no Java Views in the KMP port).

*→ Wait for answer, then ask 0.2.*

### 0.2 Screen Metric Qualifier (`DpQualifier`)

Which axis should `DpQualifier`-aware APIs use? (Affects `.sdpQualifier`, `.sdpScreen`, scaled `.sspRotate` vs strategy-prefixed Sp rotates such as `.asspRotate`, and their `code` mirrors.)

- **`SMALL_WIDTH` (default)** — smallest-width (swDP) baseline; correct for most phone/tablet layouts.
- **Explicit `WIDTH` / `HEIGHT` (or per-call-site mix)** — when design requires width-dp or height-dp branching. Read [COMPOSE-API-CONVENTIONS.md](../DOCUMENTATION/COMPOSE-API-CONVENTIONS.md) and `DpQualifier` KDoc before proposing thresholds.

Record before suggesting any `.screen`, `.qualifier`, rotate, or power-curve APIs.

*→ Wait for answer, then ask 0.3.*

### 0.3 Task Type

Disambiguate first — these are **separate** workflows:
- **Scaling strategy** = global dimension curve (`sdp`, percent, fluid, …) → Phase 1.
- **Resize** = constraint-based fit (`autoResize*`, `ResizeBound`) → Phase 3.

Ask which applies:
- **Strategy selection only** — also ask: **manual** (user picks after seeing options) or **AI-proposed** (you propose, user confirms)? Warn that automatic proposals are more error-prone on mixed form factors and resize-heavy UIs.
- **Resize only** — still complete 0.1, 0.2, and 0.4; go directly to Phase 3.
- **Both** — run Phase 1, then Phase 3.

If automatic strategy mode: confirm each major screen or module after proposing, before bulk edits.

*→ Wait for answer, then ask 0.4.*

### 0.4 Scope

Full migration (app-wide) or partial? For partial scope, list concrete paths or identifiers (files, packages, screens, composables, specific dimensions) before editing.

*→ Wait for answer, then ask 0.5.*

### 0.5 Acceptance Criteria

For each major screen or module, ask for plain-language criteria (e.g., "no clipped titles on foldable inner display", "comfortable padding on 10-inch tablet", "no overflow when the desktop window is resized small"). Tie every recommendation explicitly to these.

*→ Once answered, proceed to the relevant phase(s).*

---

## Phase 1 — Scaling Strategy Selection

*Run only when Phase 0.3 includes strategy selection. Skip for resize-only tasks with unchanged surrounding curves.*

### Default: Scaled

**`com.appdimens.kmp.compose` (scaled) / `com.appdimens.kmp.code` (scaled)** — recommend this first. Linear scaling around a **300 dp** reference on the chosen axis. Source files live under `compose/scaled/` and `code/scaled/` but the packages are top-level `compose` / `code`.

| Variant | Typical use |
|---------|-------------|
| `.sdp` | General spacing, padding, corner radii |
| `.hdp` | Vertical rhythm, row heights |
| `.wdp` | Width-driven columns |
| `.ssp` | Text; use `hsp`/`wsp` for axis variants |
| `.sdpa` / `.hdpa` | Very wide or tall screens where plain scaled is too aggressive or too conservative |

In **manual mode**: present this default and ask which UI areas use it. Accept "everything scaled except …" rules.  
In **automatic mode**: propose per screen, confirm before bulk edits.

### Other Strategies

Use only when requirements or QA justify leaving scaled. **Before recommending any strategy below**, read its matching doc in `DOCUMENTATION/` — see [library-map.md](library-map.md) § "Strategy → doc". Pull trade-offs from that doc, not from memory.

| Strategy | Role | Typically when | Not ideal when |
|----------|------|----------------|----------------|
| **Percent** | Literal fraction of a screen axis | Sizes must track sw/w/h directly (hero = 90% w) | Only need "design at 300 dp" feel — use scaled |
| **Power** | Sublinear growth, softer on large screens | Large phones/tablets feel "too big" with linear | Need predictable linear mapping everywhere |
| **Fluid** | Band behavior between reference widths | Strong control inside a width band | One simple curve app-wide without band tuning |
| **Auto** | Breakpoint-style blend (linear + log past threshold) | Clear "phone vs tablet" knee in the curve | Need mathematically smooth everywhere |
| **Diagonal** | Curve on a diagonal screen metric | Ultrawide / non-standard aspect emphasis | Simple portrait phone layouts |
| **Fill** | Cover-like growth vs reference ("bold") | Visual dominance on large canvas | Risk oversized touch targets without QA |
| **Fit** | Contain-like growth vs reference (conservative) | Prefer smaller on large screens | Need aggressive use of space on tablets |
| **Interpolated** | Piecewise curve between configured points | Hand-tuned points from design spec | No intermediate data — high maintenance |
| **Logarithmic** | Log-shaped curve, dampens growth | Strong dampening needed | Need linear proportional feel |
| **Perimeter** | Perimeter-style metric in formula | Designs keyed to "frame" perception | No such requirement |
| **Density** | Classic density-style scaling | Matching legacy dp-to-physical expectations | Want screen-shape-aware curves |
| **Physical units** | mm / cm / in helpers | Real-world sizing (rulers, print-like UI) | Most Material layout — use scaled instead |
| **Resize** | *(not a global curve)* Largest discrete step in [min,max] fitting constraints | Auto-fit text or boxes inside known max | Whole-screen proportional layout |

---

## Phase 2 — Implementation Rules

Apply regardless of strategy or UI stack:

1. **Strategy isolation** — `com.appdimens.kmp.compose.<strategy>` and `com.appdimens.kmp.code.<strategy>` do not cross-import. One strategy per calculation path.
2. **`AppDimensProvider`** — required for Compose facilitators like `.sdpMode`, `.sdpScreen`, `.sspScreen` (see README). Works on **all platforms**: on Android it wraps the live `Context`; on desktop/web/iOS/macOS it rebuilds the snapshot from the live window configuration, so resize self-heals.
3. **Config churn** — since 1.0.0 no explicit invalidation is required for correctness: every resolution is keyed by the immutable per-window `DimenMetrics` snapshot, so a rotated/resized/recreated window can never read a stale value. `DimenCache.invalidateOnConfigChange` is retained as a compatibility hook only.
4. **`code` hot paths** — prefer `Int`/`Float` receivers for sdp/hdp/wdp to avoid boxing. Pass the `AppDimensContext` (obtainable inside composition via `localAppDimensContext()`).
5. **Nested Plain facilitators** — use `Dp`/`TextUnit` alternates in `*Plain` chains to avoid double-scaling (README + COMPOSE-API-CONVENTIONS).
6. **Multiplatform** — everything is `commonMain`-safe: no Android-only API (e.g. `Context`, `LocalConfiguration`) in shared code. If a platform adapter is needed, use `expect`/`actual` in the right source set (`androidMain` / `jvmMain` / `iosMain` / `macosMain` / `wasmJsMain` / `nativeMain`).

---

## Phase 3 — Resize Work

*Run when the user touches `autoResize*` APIs, `ResizeBound`, `ResizeRangePx`, or `DimenResize`.*

Ask in order, waiting for each answer before continuing:

1. **Element type** — text (font size sweep, sp) or non-text component (heights, widths, square sizes, images, cards)?
2. **Constraint source** — Compose: must run inside `BoxWithConstraints` (max width/height in dp). `code`: how is max px/dp obtained?
3. **Expected behavior** — fill to cap, shrink to fit, or prefer a size? Any min/max in dp, sp, axis percent (screen), or percent of inner box?
4. **Bounds semantics** — clarify `ResizeBound.FixedDp` / `FixedSp` / `Percent` (sw/w/h axis). For percent-in-container resize, clarify `AutoResizePercentBasis` (min/max side, width, height) and use `autoResizeTextSpPercent` (Compose) or `fittingTextSpPercentPx` (code). Remind: `resolveToPx` requires `density > 0`; invalid inputs are clamped (library KDoc).
5. **Approach check** — global proportional sizing across the screen is usually scaled/percent/fluid, not resize. Reserve resize for fit-to-container problems.

Reference: [DOCUMENTATION/resize.md](../DOCUMENTATION/resize.md)

---

## Phase 4 — Execution Checklist

- [ ] Agent Preflight completed for touched symbols.
- [ ] Phase 0 answered: stack, `DpQualifier` baseline, task type (strategy / resize / both), scope, acceptance criteria.
- [ ] Scaled presented as default; other strategies have matching `DOCUMENTATION/*.md` opened and trade-offs cited.
- [ ] `ignoreMultiWindows` / `*i` / `*ia` not surfaced to user.
- [ ] Docs read for anything beyond trivial one-liners.
- [ ] Imports use `com.appdimens.kmp.*` (never `com.appdimens.dynamic.*`); Maven coordinates use `appdimens-kmp`.
- [ ] Diffs are small and reviewable; aligned with upstream example patterns.
- [ ] Build / lint the touched module if available: `./gradlew :library:jvmTest` (or the affected `:library-<strategy>:jvmTest`), `:library:testAndroidHostTest`, and a compile check of the touched target (e.g. `:library:compileKotlinWasmJs` / `compileKotlinIosArm64`).

---

## Output Style

For every decision state: chosen strategy, package family (`compose.*` or `code.*`), axis (`sdp` vs `hdp` vs `wdp`), whether resize vs global scaling, and which acceptance criterion it satisfies. Prefer small, reviewable diffs aligned with existing project patterns.
