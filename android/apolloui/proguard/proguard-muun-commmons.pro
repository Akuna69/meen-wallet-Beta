-keepnames class io.meen.common.api.**
-keep class io.meen.common.api.** { *; }
-keepclassmembers class io.meen.common.api.** { *; }
-keepclassmembernames class io.meen.common.api.** { *; }

-keepnames class io.meen.common.model.UserPreferences
-keep class io.meen.common.model.UserPreferences { *; }
-keepclassmembers class io.meen.common.model.UserPreferences { *; }
-keepclassmembernames class io.meen.common.model.UserPreferences { *; }

-keepnames class io.meen.common.api.messages.**
-keep class io.meen.common.api.messages.** { *; }
-keepclassmembers class io.meen.common.api.messages.** { *; }
-keepclassmembernames class io.meen.common.api.messages.** { *; }

-keepnames class io.meen.common.model.SizeForAmount
-keep class io.meen.common.model.SizeForAmount { *; }
-keepclassmembers class io.meen.common.model.SizeForAmount { *; }
-keepclassmembernames class io.meen.common.model.SizeForAmount { *; }

-keepnames class io.meen.common.utils.Pair
-keep class io.meen.common.utils.Pair { *; }
-keepclassmembers class io.meen.common.utils.Pair { *; }
-keepclassmembernames class io.meen.common.utils.Pair { *; }

-keep public enum io.meen.common.model.SessionStatus$** {
    **[] $VALUES;
    public *;
}