# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\adithya.upadhya\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes *Annotation*, Exceptions, Signature, Deprecated, SourceFile, SourceDir, LineNumberTable, LocalVariableTable, LocalVariableTypeTable, Synthetic, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, AnnotationDefault, InnerClasses

# Proguard rules for Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule


# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-dontwarn  com.fasterxml.jackson.databind.**
-dontnote  com.fasterxml.jackson.databind.**

-keep class com.adithyaupadhya.newtorkmodule.volley.pojos.** { *; }



# Proguard rules for material dialog
-keepnames class com.afollestad.materialdialogs.** { *; }
-keep class com.afollestad.materialdialogs.** { *; }
-keep interface com.afollestad.materialdialogs.** { *; }
-dontwarn com.afollestad.materialdialogs.**


-keep class java.lang.Class.getDeclaredField
-keep  class * implements java.io.Serializable {
	*;
}


# Proguard rules for OkHttp and Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn java.nio.file.**


# Proguard rules for Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**