################################################################################
# AppDimens Dynamic (KMP) — consumer-rules.pro
#
# Bundled INSIDE the Android AAR of the KMP library and merged into the
# consuming app's R8/ProGuard run when:
#
#   implementation(project(":library"))  (or the published KMP artifact)
#
# Ported from the Android original (appdimens-dynamic/library/consumer-rules.pro).
# Design principle (3.1.8): keep ONLY what is proven necessary. Direct bytecode
# references — including @PublishedApi internals reached through inlined function
# bodies — are discovered by R8 full mode on its own. Everything below exists
# because a real runtime failure (or a deliberate performance contract) was
# demonstrated without it.
################################################################################


################################################################################
# 1. PUBLIC API SURFACE
#    -keepnames (not -keep): unreachable members may still be removed, but names
#    used by Java/Kotlin call sites that resolved at compile time are never
#    renamed (mapping-file consumers and non-inlined binaries depend on it).
################################################################################

-keepnames public class com.appdimens.kmp.code.** { public protected *; }
-keepnames public class com.appdimens.kmp.compose.** { public protected *; }
-keepnames public class com.appdimens.kmp.common.** { public protected *; }


################################################################################
# 2. KOTLIN METADATA
################################################################################

-keep class kotlin.Metadata { *; }


################################################################################
# 3. CACHE-KEY ENUMS
#    DpQualifier, Inverter, UiModeType, UnitType ordinals are encoded into cache
#    keys and used in when-expressions throughout the builder chain. Renaming
#    entries causes wrong dispatch and silent scaling errors.
################################################################################

-keepclassmembers enum com.appdimens.kmp.common.* {
    <fields>;
    <methods>;
}

# AutoResizePercentBasis ordinals are used in resize math.
-keepnames class com.appdimens.kmp.core.AutoResizePercentBasis { *; }


################################################################################
# 4. SEALED CLASS — ResizeBound
#    R8 full mode eliminates sealed subclasses it never sees instantiated in the
#    current analysis scope; the resize helpers instantiate them indirectly.
################################################################################

-keepnames class com.appdimens.kmp.core.ResizeBound { *; }
-keepnames class com.appdimens.kmp.core.ResizeBound$* { *; }
-keepnames class com.appdimens.kmp.core.ResizeBoundKt { *; }


################################################################################
# 5. ScreenFactors PADDING FIELDS (_p0.._p7)
#    R8 full mode strips @JvmField-style fields it identifies as write-only.
#    These fields are never read by name — their only purpose is to occupy
#    memory and prevent CPU false sharing on ARM64 (multi-core hot paths).
#    Losing them silently undoes the padding contract without any crash.
################################################################################

-keepclassmembers class com.appdimens.kmp.core.DimenCache$ScreenFactors {
    <fields>;
}


################################################################################
# 6. SUPPRESS NOTES — full mode is noisier than compat mode
################################################################################

-dontnote android.**
-dontnote androidx.**
-dontnote kotlin.**
-dontnote kotlinx.**
-dontwarn sun.misc.**
-dontnote sun.misc.**
