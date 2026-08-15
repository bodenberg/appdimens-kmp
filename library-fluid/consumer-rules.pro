################################################################################
# AppDimens Dynamic satellite — appdimens-kmp-fluid
# Core R8 rules arrive transitively via appdimens-kmp (main).
################################################################################

-keepnames public class com.appdimens.kmp.code.fluid.** { public protected *; }
-keepnames public class com.appdimens.kmp.compose.fluid.** { public protected *; }

################################################################################
# Satellite internal helpers are NOT kept (static-only references, no
# reflection) so the app's R8 pass may shrink them freely.
################################################################################
