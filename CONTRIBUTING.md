# Contributing to AppDimens (DYNAMIC KMP)

First off, thank you for considering contributing to AppDimens! It’s contributions like yours that make the developer experience better for everyone handling **screen fragmentation across Android, desktop (JVM), iOS, macOS, web (Kotlin/JS + wasmJs), Linux and Windows**.

## How Can I Contribute?

### 1. Reporting Bugs 🐛

If you find a calculation error, a rendering glitch, or a crash:

* Check the [Issues](https://www.google.com/search?q=https://github.com/bodenberg/appdimens-sdps/issues) page to see if it has already been reported.
* If not, open a new issue. Please include:
  * The device model / screen density (DPI) **and platform** (Android, JVM, iOS, macOS, Web, Linux, Windows) where the issue occurred.
  * A brief code snippet or layout showing how the library was used.
  * The expected vs. actual result.

### 2. Suggesting Enhancements 💡

Have an idea for a new scaling strategy or support for more screen buckets?

* Open an issue with the tag `enhancement`.
* Explain the use case and how it benefits other developers using the SDPS pattern.

### 3. Pull Requests (PRs) 🚀

Ready to contribute code? Follow these steps:

1. **Fork** the repository.
2. Create a **Branch** for your feature or fix (`git checkout -b feature/amazing-feature`).
3. Commit your changes with clear messages (e.g., `fix: adjust scaling for xxhdpi tablets`).
4. **Push** to the branch (`git push origin feature/amazing-feature`).
5. Open a **Pull Request**.

## Technical Guidelines

* **Code Style:** Follow standard Kotlin/Compose coding conventions (the project uses `kotlin.code.style=official`).
* **Modules:** Strategy code lives in `:library-<strategy>`; shared cache/core/scaled/plain stay in `:library`. Satellites depend only on `:library`. `:library-bom` publishes version constraints only. See [DOCUMENTATION/MODULES.md](DOCUMENTATION/MODULES.md).
* **Multiplatform:** the library targets **Android, JVM, iOS (`iosArm64`/`iosSimulatorArm64`), macOS (`macosArm64`) and wasmJs (browser)**. New code must compile for **all** targets — keep platform logic behind `expect/actual` in `androidMain` / `jvmMain` / `iosMain` / `macosMain` / `wasmJsMain` (or `nativeMain` for shared native code).
* **Documentation:** If you add a new scaling method, parameter, or module, update the README installation matrix, the strategy page under `DOCUMENTATION/`, and [MODULES.md](DOCUMENTATION/MODULES.md). Keep KDoc on public APIs (the project uses bilingual EN/PT KDoc).
* **Tests:** Run the JVM tests and the Android host tests:

  ```bash
  ./gradlew :library:jvmTest :library:allTests
  ./gradlew :library-auto:jvmTest :library-percent:jvmTest   # affected satellites
  ```

  Compose-based browser tests need a browser (run `:library:wasmJsBrowserTest` locally if you have Chrome/Chromium).
* **Precision:** Since this library focuses on UI precision, ensure that any changes to dimension calculations are tested across multiple screen configurations — the shared `commonTest` suites (`CoreMathTests`, `DimenFastLaneKernelsTest`, per-strategy formula tests) run on JVM and should stay green.
* **Performance:** Hot-path changes must not add allocations or locks to the fast lane (single multiply over precomputed per-window factors). The `benchlab` module measures this on device; keep `DimenPerformanceTest` green.

## License

By contributing, you agree that your contributions will be licensed under the project's **Apache License 2.0**.
