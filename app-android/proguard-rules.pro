################################################################################
# AppDimens Demo (KMP) — ProGuard / R8 rules
#
# Ported from the Android original (appdimens-dynamic/app/proguard-rules.pro):
# AGP 9 runs R8 in full mode, where ALL optimization types (code, method,
# field, class, library) are already enabled — the legacy -optimizations flag
# is ignored, so it is not listed here. What still matters is the number of
# optimizer iterations (-optimizationpasses; default 1) — more passes squeeze
# the hot dimension-scaling paths further for a small build-time cost — and
# -allowaccessmodification so R8 may inline across visibility boundaries.
################################################################################

-optimizationpasses 10
-allowaccessmodification

################################################################################
# KEEPS
################################################################################

-keepattributes Annotation,Exceptions,LineNumberTable,Signature,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault
-keep class kotlin.Metadata { *; }

-keepnames class * implements android.os.Parcelable
-keepnames interface * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements java.io.Serializable
-keepnames interface * implements java.io.Serializable

-keepclasseswithmembernames,includedescriptorclasses class * { native <methods>; }
-keep class sun.misc.Unsafe.** { *; }

################################################################################
# LOGS DEBUG — mapping/usage reports for R8 auditing
################################################################################
-printseeds build/outputs/mapping/release/seeds.txt
-printmapping build/outputs/mapping/release/mapping.txt
-printconfiguration build/outputs/mapping/release/configuration.txt
-printusage build/outputs/mapping/release/usage.txt
-verbose
