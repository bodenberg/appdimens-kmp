# R8, full mode, and ProGuard rules (AppDimens Dynamic KMP)

This guide explains **why** and **how** to use R8/minify, **what changes** when you turn on **full mode**, and **which ProGuard rule file does what** in this repository. It is written for developers who are new to R8/ProGuard but want accurate, technical detail.

> [!NOTE]
> **KMP note:** the R8 pipeline below applies to the **Android** target of this Kotlin Multiplatform library (the Android AARs). The **JVM, iOS, macOS and wasmJs** targets ship as plain JVM jars / Kotlin/Native klibs / wasm binaries and are optimized by their respective toolchains (JIT/C2, Kotlin/Native `-opt`, wasm optimizer). The **consumer rules** documented here are what protects the Android target inside consumer apps.

---

## Consumer rules per artifact

Each published Android AAR ships its own `consumer-rules.pro`:

- **`appdimens-kmp`** — `common`, scaled `code`/`compose`, plus the few proven-specific core rules (cache-key enums, `ResizeBound`, `ScreenFactors` padding, `kotlin.Metadata`)
- **`appdimens-kmp-<strategy>`** — that strategy’s public `code` / `compose` API (core rules arrive transitively)

Gradle merges consumer rules from the modules you depend on.

## Why use minify and R8?

**R8** runs when you enable **code shrinking** (often called “minify”) on a **release** (or other) build type. In practice you get three related benefits:

1. **Smaller APK/AAB** — Unused classes, methods, and fields from your app **and** from dependencies can be removed (*tree shaking*). Users download less; install footprint is smaller.
2. **Faster runtime on device** — R8 can **inline** calls, simplify control flow, and remove indirection. For CPU-heavy libraries (like dimension scaling on hot UI paths), that can show up as **lower nanoseconds per call** in benchmarks. This repo documents that effect in [`PERFORMANCE.md`](PERFORMANCE.md) and [`PERFORMANCE-COMPARATIVE.md`](PERFORMANCE-COMPARATIVE.md).
3. **Obfuscation** — Short names replace original class/member names in the shipped bytecode. This is **not** encryption, but it makes casual reverse engineering harder and complements Play App Signing.

**R8 full mode** (`android.enableR8.fullMode=true`) asks the toolchain for **stricter** optimizations than “compat” mode. You may get **even better** size and speed, but **more** code paths are candidates for removal or renaming — so **correct ProGuard rules matter more**. (On AGP 9+ full mode is always on; the property is a documented no-op kept for parity with the Android original.)

**Trade-off in one sentence:** you exchange a bit of build time and configuration care for smaller binaries and often faster, harder-to-read release code.

---

## How to enable and use it

### 1. Turn on minify for your **application** module

In the module that produces the installable APK (e.g. `:app-android`), set **`minifyEnabled`** (Kotlin DSL: **`isMinifyEnabled`**) to **`true`** for the build type you ship to users — typically **`release`**.

Example pattern (your file may differ slightly):

```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            // Optional: remove unused resources after code shrink
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

In **this** repository, `app-android` and `benchlab-android` already do this. Your own project needs the same idea on **your** Android app module.

### 2. (Optional) Enable **R8 full mode** for the whole project

In the root [`gradle.properties`](gradle.properties), the property is present:

```properties
android.enableR8.fullMode=true
```

On AGP 9+ this is a documented no-op (full mode is always on); it is kept for parity with the Android original and for older AGP setups.

### 3. Rules: what you edit vs what you get for free

- **Your app:** put app-specific keeps in **`app-android/proguard-rules.pro`** (reflection, JNI, serialization libraries, etc.).
- **AppDimens Dynamic KMP:** each AAR you depend on ships its own `consumer-rules.pro`, merged by Gradle automatically. Core/scaled rules come from `appdimens-kmp`; strategy rules come from each `appdimens-kmp-<strategy>` AAR.

If a release build **crashes** only after turning on minify or full mode, open **R8 / ProGuard** mapping or stack traces, then adjust **keeps** (see sections below).

---

## Risks specific to R8 full mode

Full mode is **optional**. If you enable it, expect the same categories of issues as minify — just **more likely** until rules are tight. R8 then applies **stricter** shrinking, optimization, and obfuscation. That is usually good for size and speed, but it can:

- Remove or rename members that look “unused” to R8 but are still required at runtime (for example **Kotlin `inline` bodies** expanded in the **app** that reference **`internal` APIs** marked `@PublishedApi`).
- Strip **padding fields** that exist only to avoid **false sharing** on multi-core CPUs, silently hurting hot-path performance.
- Break **Compose** wiring (`CompositionLocal`, providers) if keep rules are incomplete.

**You must keep the library’s consumer rules in sync** and test a **release** build (minify on) on a real device. If something crashes with `NoSuchFieldError`, `NoSuchMethodError`, or missing composables, the fix is almost always an extra `-keep` or a corrected rule scope — not “disable R8 forever.”

See also: [`library/PERFORMANCE.md`](library/PERFORMANCE.md) (cache notes) and the performance reports [`PERFORMANCE.md`](PERFORMANCE.md) / [`PERFORMANCE-COMPARATIVE.md`](PERFORMANCE-COMPARATIVE.md).

---

## R8 and ProGuard rules in plain terms

**R8** is the Android toolchain step that can **shrink** (delete unused code), **optimize** (inline, simplify), and **obfuscate** (rename classes/members) your compiled bytecode.

**ProGuard rule files** (`.pro`) are text instructions such as “do not remove this class” (`-keep`) or “allow these optimizations” (`-optimizations`). Gradle merges **default rules**, **your app rules**, and **rules shipped inside AARs** (`consumer-rules`) into one configuration for the app’s release build.

Think of it as: **R8 reads your app + dependencies + merged rules → outputs a smaller, faster, possibly renamed program.** If a rule is wrong, the program still builds but can fail at runtime.

---

## 1. Library module: `library/proguard-rules.pro` (and each satellite's)

**When it runs:** At **AAR build time** for the **release** variant. Every AppDimens KMP library module (`:library`, `:library-auto`, …) pre-shrinks and pre-optimizes its Android AAR via the AGP KMP plugin's `optimization` block:

```kotlin
android {
    optimization {
        minify = true
        keepRules.files("proguard-rules.pro")
        consumerKeepRules.files("consumer-rules.pro")
    }
}
```

**What this file is for:**

- Aggressive **optimization**: `-optimizationpasses 10` (more optimizer iterations) and `-allowaccessmodification` (relaxed visibility so R8 can inline across boundaries).
- **`-dontobfuscate`**: the AAR is **shrunk and optimized, never renamed** (AndroidX-style). Renaming still happens once, in the **consuming app's own R8 pass**, where the app's mapping file applies.
- **Keeps** = the cross-module contract, now with **`allowoptimization`** (a bare `-keep` also forbids R8 from *optimizing* the kept members, and the hot path lives inside them):
  - public API packages (`code.**`, `compose.**`, `common.**`) — kept, so a published AAR never strips public members, but their bodies stay optimizable;
  - **the whole `core.**` engine** — satellites compile against it and inline its `@PublishedApi` members; a member removed or renamed here would fail only at consumer runtime, so none may be removed, but all may be optimized.

**Result:** every Android app that depends on AppDimens KMP gets pre-optimized bytecode — even in **debug** builds without minify — and the app's own release R8 pass shrinks the remaining surface per-app (via `consumer-rules.pro`).

> **Verified:** all 14 AARs ship `proguard.txt` with R8-processed classes (e.g. the percent module removed 32 dead members), and the app's R8 config merges **29 consumer rules** (main + 13 satellites).

---

## 2. Consumer rules: `library/consumer-rules.pro`

**When it runs:** Automatically. This file is registered with `consumerKeepRules.files(...)` in the module's `optimization` block. Gradle **packages** it into the **AAR**. When an app enables **minify/R8** on **release**, those rules are **merged** into the app’s R8 configuration **without** the app author copying anything.

**What this file is for:**

- The **minimum** set of rules so the **published library** stays correct inside **any** consuming app under R8 (including **full mode**).
- The file is **almost empty by design**: the `@PublishedApi` internals reached through inlined extension bodies are expanded into the app's bytecode at compile time, so R8 discovers them as plain direct references and keeps them automatically.
- Remaining specific rules, each with a demonstrated runtime or performance failure behind it:
  - public API surfaces (`-keepnames`: the app's R8 may drop library members the app does not use, but may never rename them);
  - `kotlin.Metadata` (reflective reads by Compose tooling / `kotlinx.reflect`);
  - enums whose **ordinals are packed into cache keys** (renaming breaks dispatch);
  - the `ResizeBound` sealed hierarchy (R8 full mode can eliminate uninstantiated sealed subclasses → `ClassNotFoundException`);
  - `ScreenFactors` **padding fields** (write-only by design; a plain `-keep` prevents R8 from stripping the ARM64 false-sharing protection).

**Beginner mental model:**  
`proguard-rules.pro` (library) = “when we minify **the library artifact** itself.”  
`consumer-rules.pro` = “when **someone else’s app** minifies **their** APK that contains our AAR.”

---

## 3. App module: `app-android/proguard-rules.pro`

**When it runs:** When the **`:app-android`** module has `minifyEnabled = true` (release).

**What this file is for:**

- Rules for the **sample application**: `-optimizationpasses 10` + `-allowaccessmodification` (more R8 iterations + cross-visibility inlining), keepattributes for annotation/line-number/signature retention, and mapping/usage/configuration report generation (`-printmapping`, `-printusage`, …).
- It works **together** with:
  - Android Gradle **default** ProGuard/R8 files (`proguard-android-optimize.txt`, etc.);
  - **Merged consumer rules** from dependencies (including every AppDimens KMP module's `consumer-rules.pro`).

**Important:** Your **own** app will have **its own** `proguard-rules.pro`. The sample under `app-android/` is **not** a substitute for rules your code may need (reflection, Gson, Hilt, etc.). AppDimens only ships what the library needs via **consumer** rules; everything else is your responsibility.

---

## Quick checklist (release + R8)

1. The **published AARs are already shrink+optimized** (all 14 modules run R8 at AAR build time with `-dontobfuscate`); the checks below still apply to the **consumer app**'s own pass.
2. Build and run **`release`** (or minified) on a device — not only `debug`.
3. Exercise screens that use **Compose** extensions, **resize** APIs, and **configuration changes**.
4. If you enable **`android.enableR8.fullMode=true`**, repeat the same tests; failures usually mean a missing or overly narrow `-keep`. (On AGP 9+ full mode is always on and this flag is a no-op.)
5. For performance comparisons, see [`PERFORMANCE.md`](PERFORMANCE.md): **debug without minify** vs **release with minify + R8** are different environments; do not mix numbers without reading the methodology note.

---

*AppDimens Dynamic KMP — R8 / ProGuard reference (Android target)*
