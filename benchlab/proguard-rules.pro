################################################################################
# BenchLab — ProGuard / R8 rules
################################################################################

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
