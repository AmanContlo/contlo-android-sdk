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

# Specifies the JVM language level whose features are available to the code.
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Keep source file names and line numbers in the obfuscated code.
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Preserve all annotations.
-keepattributes *Annotation*
-printmapping mapping.txt

# Keep the package name of the application.
-keepattributes 'ApplicationName'
-keepattributes 'ModuleName'

# Don't remove any code that is used by the Android system or is referenced from the AndroidManifest.xml file.
-dontoptimize
-dontshrink

# Obfuscate all class names
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt

# Keep all public classes and members.
-keep public class * {
    public *;
}

# Keep support libraries.
-keep class android.support.** {*;}
-keep interface android.support.** {*;}

# Keep any classes that are used through reflection.
-keepclassmembers class * {
    *** *();
    *** *($*);
}

