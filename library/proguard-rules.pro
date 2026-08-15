################################################################################
# AppDimens Dynamic (KMP) — proguard-rules.pro (library module)
#
# THIS FILE IS LIVE: the :library android AAR is MINIFIED AND OPTIMIZED AT
# BUILD TIME via the AGP KMP `optimization { minify = true; keepRules ... }`
# block — mirroring the Android original, where the release build type sets
# `isMinifyEnabled = true`. Every consumer app therefore gets pre-optimized
# bytecode even before its own R8 pass.
#
# Design decisions (ported from the Android original):
#
#  1. -dontobfuscate
#     Names are never renamed at library build time (AndroidX-style).
#     Renaming happens once, in the CONSUMER APP's own R8 pass.
#
#  2. -keep,allowoptimization (NOT bare -keep)
#     A bare `-keep` forbids R8 from OPTIMIZING the kept members. The members
#     that matter most for the hot path — DimenCache, DimenMetrics, the
#     plumbing, the scaled kernel — live under the kept surface.
#     `allowoptimization` lets R8 optimize their method bodies (constant
#     folding, branch simplification, inlining) while still forbidding
#     removal/renaming, so the cross-module ABI stays linkable.
#
#  3. Keeps = the whole cross-module contract:
#     - public .code.** / .compose.** / .common.** API (apps call by name)
#     - everything in .core.** — satellite AARs reference core directly AND
#       via @PublishedApi inlined bodies compiled at their own build time.
#
#  consumer-rules.pro is a SEPARATE contract: it runs in the CONSUMING APP's
#  R8 pass and deliberately allows the app to drop unused library members.
################################################################################

-dontobfuscate

-optimizationpasses 10
-allowaccessmodification


################################################################################
# 1. PUBLIC API SURFACE — classes apps call by name (Kotlin extensions,
#    @Composable functions, Java-style statics). Full -keep: a published AAR
#    must not strip public members; only the consuming app may decide they are
#    unused (it does, via -keepnames in consumer-rules.pro). allowoptimization
#    keeps their bodies optimizable.
################################################################################

-keep,allowoptimization public class com.appdimens.dynamic.code.** { public protected *; }
-keep,allowoptimization public class com.appdimens.dynamic.compose.** { public protected *; }
-keep,allowoptimization public class com.appdimens.dynamic.common.** { public protected *; }


################################################################################
# 2. CORE — the whole cross-module contract (satellites + inlined @PublishedApi
#    bodies reference it). None may be removed or renamed; optimization is fine.
################################################################################

-keep,allowoptimization class com.appdimens.dynamic.core.** { *; }


################################################################################
# 3. KOTLIN METADATA
################################################################################

-keep class kotlin.Metadata { *; }
