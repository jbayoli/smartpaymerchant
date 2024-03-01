# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes SourceFile,LineNumberTable

-dontwarn org.slf4j.**

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.* { *; }
-dontwarn okio.**

# Acceptance Devices
-keep class io.mpos.** { *; }
-dontwarn io.mpos.**
-keep class com.visa.vac.tc.** {*;}
-keep class com.nimbusds.jose.** {*;}
-keep class org.bouncycastle.** {*;}

-keep class com.mastercard.sonic.BuildConfig {*;}

-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

-keep class kotlin.coroutines.Continuation

-dontwarn java.sql.JDBCType
-dontwarn javax.xml.bind.DatatypeConverter
