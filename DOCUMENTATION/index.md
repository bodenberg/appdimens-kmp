# AppDimens Dynamic KMP — Package index

For product scope, architecture, and formal math, see also:

| Document | Description |
|----------|-------------|
| [PRD.md](PRD.md) | Product requirements (FR/NFR) |
| [PDR.md](PDR.md) | Technical design, development plan, traceability matrix |
| [MATHEMATICS-AND-CALCULUS.md](MATHEMATICS-AND-CALCULUS.md) | Formulas, constants, the `DimenMetrics` window snapshot + per-strategy calculation kernels |
| [**Modules** — Maven/Gradle graph](MODULES.md) | Artifact matrix and install (`1.0.0`) |

Narrative strategy guides and the full doc hub: [README.md](README.md).

## Packages

Every strategy ships both a **Compose** package (`com.appdimens.dynamic.compose.<strategy>`) and a non-Compose **`code`** package (`com.appdimens.dynamic.code.<strategy>`), with **no cross-imports** between strategies. Scaled and plain live in the principal artifact; the other strategies live in their own Maven module.

| Package | Artifact | Strategy doc |
|---|---|---|
| `com.appdimens.dynamic.common` | principal | — |
| `com.appdimens.dynamic.core` | principal | — |
| `com.appdimens.dynamic.code` / `compose` | principal | [scaled.md](scaled.md) |
| `com.appdimens.dynamic.code.plain` | principal | — |
| `com.appdimens.dynamic.code.auto` / `compose.auto` | `appdimens-dynamic-auto` | [auto.md](auto.md) |
| `com.appdimens.dynamic.code.density` / `compose.density` | `appdimens-dynamic-density` | [density.md](density.md) |
| `com.appdimens.dynamic.code.diagonal` / `compose.diagonal` | `appdimens-dynamic-diagonal` | [diagonal.md](diagonal.md) |
| `com.appdimens.dynamic.code.fill` / `compose.fill` | `appdimens-dynamic-fill` | [fill.md](fill.md) |
| `com.appdimens.dynamic.code.fit` / `compose.fit` | `appdimens-dynamic-fit` | [fit.md](fit.md) |
| `com.appdimens.dynamic.code.fluid` / `compose.fluid` | `appdimens-dynamic-fluid` | [fluid.md](fluid.md) |
| `com.appdimens.dynamic.code.interpolated` / `compose.interpolated` | `appdimens-dynamic-interpolated` | [interpolated.md](interpolated.md) |
| `com.appdimens.dynamic.code.logarithmic` / `compose.logarithmic` | `appdimens-dynamic-logarithmic` | [logarithmic.md](logarithmic.md) |
| `com.appdimens.dynamic.code.percent` / `compose.percent` | `appdimens-dynamic-percent` | [percent.md](percent.md) |
| `com.appdimens.dynamic.code.perimeter` / `compose.perimeter` | `appdimens-dynamic-perimeter` | [perimeter.md](perimeter.md) |
| `com.appdimens.dynamic.code.power` / `compose.power` | `appdimens-dynamic-power` | [power.md](power.md) |
| `com.appdimens.dynamic.code.resize` / `compose.resize` | `appdimens-dynamic-resize` | [resize.md](resize.md) |
| `com.appdimens.dynamic.code.units` / `compose.units` | `appdimens-dynamic-units` | [physical-units.md](physical-units.md) |
