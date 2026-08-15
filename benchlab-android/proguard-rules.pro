################################################################################
# BenchLab (KMP) — ProGuard / R8 rules
#
# Ported from the Android original (appdimens-dynamic/benchlab/proguard-rules.pro)
# with the app-level aggressive optimization settings added (see the Android
# original app/proguard-rules.pro): -optimizationpasses 10 + -allowaccessmodification
# squeeze the measured hot paths further in release builds.
################################################################################

-optimizationpasses 10
-allowaccessmodification

################################################################################
# KEEPS
################################################################################

-keepattributes Annotation,Exceptions,LineNumberTable,Signature,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault
-keep class kotlin.Metadata { *; }

# Keep the benchmark activity and all Compose UI
-keep class com.example.benchlab.** { *; }
-dontwarn com.example.benchlab.**

# AppDimens Dynamic KMP library
-keep class com.appdimens.dynamic.** { *; }
-dontwarn com.appdimens.dynamic.**

# Concorrente 2 (Lib #2 — KMP artifact)
-keep class network.chaintech.** { *; }
-dontwarn network.chaintech.**

# Compose
-dontwarn androidx.compose.**

# Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

################################################################################
# LOGS DEBUG — mapping/usage reports for R8 auditing
################################################################################
-printseeds build/outputs/mapping/release/seeds.txt
-printmapping build/outputs/mapping/release/mapping.txt
-printconfiguration build/outputs/mapping/release/configuration.txt
-printusage build/outputs/mapping/release/usage.txt
-verbose
