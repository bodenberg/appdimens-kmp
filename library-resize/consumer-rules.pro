################################################################################
# AppDimens Dynamic satellite — appdimens-kmp-resize
# Core R8 rules arrive transitively via appdimens-kmp (main).
################################################################################

-keepnames public class com.appdimens.kmp.code.resize.** { public protected *; }
-keepnames public class com.appdimens.kmp.compose.resize.** { public protected *; }

################################################################################
# Satellite internal helpers are NOT kept (static-only references, no
# reflection) so the app's R8 pass may shrink them freely.
################################################################################
