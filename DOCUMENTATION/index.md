# AppDimens Dynamic KMP — Package index

For product scope, architecture, and formal math, see also:

| Document | Description |
|----------|-------------|
| [PRD.md](PRD.md) | Product requirements (FR/NFR) |
| [PDR.md](PDR.md) | Technical design, development plan, traceability matrix |
| [MATHEMATICS-AND-CALCULUS.md](MATHEMATICS-AND-CALCULUS.md) | Formulas, constants, the `DimenMetrics` window snapshot + per-strategy calculation kernels |
| [**Modules** — Maven/Gradle graph](MODULES.md) | Artifact matrix and install (`1.0.1`) |
| [**Skill** — `appdimens-kmp-workflow`](../skills/SKILL.md) | Agent workflow for AppDimens KMP integration (Compose vs `code`, scaling vs resize, multi-platform preflight); companion [reference.md](../skills/reference.md) and [library-map.md](../skills/library-map.md) (package map, strategy → doc links at Git ref `1.0.1`) |

Narrative strategy guides and the full doc hub: [README.md](README.md).

## Packages

Every strategy ships both a **Compose** package (`com.appdimens.kmp.compose.<strategy>`) and a non-Compose **`code`** package (`com.appdimens.kmp.code.<strategy>`), with **no cross-imports** between strategies. Scaled and plain live in the principal artifact; the other strategies live in their own Maven module.

| Package | Artifact | Strategy doc |
|---|---|---|
| `com.appdimens.kmp.common` | principal | — |
| `com.appdimens.kmp.core` | principal | — |
| `com.appdimens.kmp.code` / `compose` | principal | [scaled.md](scaled.md) |
| `com.appdimens.kmp.code.plain` | principal | — |
| `com.appdimens.kmp.code.auto` / `compose.auto` | `appdimens-kmp-auto` | [auto.md](auto.md) |
| `com.appdimens.kmp.code.density` / `compose.density` | `appdimens-kmp-density` | [density.md](density.md) |
| `com.appdimens.kmp.code.diagonal` / `compose.diagonal` | `appdimens-kmp-diagonal` | [diagonal.md](diagonal.md) |
| `com.appdimens.kmp.code.fill` / `compose.fill` | `appdimens-kmp-fill` | [fill.md](fill.md) |
| `com.appdimens.kmp.code.fit` / `compose.fit` | `appdimens-kmp-fit` | [fit.md](fit.md) |
| `com.appdimens.kmp.code.fluid` / `compose.fluid` | `appdimens-kmp-fluid` | [fluid.md](fluid.md) |
| `com.appdimens.kmp.code.interpolated` / `compose.interpolated` | `appdimens-kmp-interpolated` | [interpolated.md](interpolated.md) |
| `com.appdimens.kmp.code.logarithmic` / `compose.logarithmic` | `appdimens-kmp-logarithmic` | [logarithmic.md](logarithmic.md) |
| `com.appdimens.kmp.code.percent` / `compose.percent` | `appdimens-kmp-percent` | [percent.md](percent.md) |
| `com.appdimens.kmp.code.perimeter` / `compose.perimeter` | `appdimens-kmp-perimeter` | [perimeter.md](perimeter.md) |
| `com.appdimens.kmp.code.power` / `compose.power` | `appdimens-kmp-power` | [power.md](power.md) |
| `com.appdimens.kmp.code.resize` / `compose.resize` | `appdimens-kmp-resize` | [resize.md](resize.md) |
| `com.appdimens.kmp.code.units` / `compose.units` | `appdimens-kmp-units` | [physical-units.md](physical-units.md) |
