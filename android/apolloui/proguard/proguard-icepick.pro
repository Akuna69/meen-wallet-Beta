#Icepick

-dontwarn icepick.**
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}

-keepnames class io.meen.apollo.presentation.ui.** { @icepick.State *;}