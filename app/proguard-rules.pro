# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android\sdk/tools/proguard/proguard-android.txt
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

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/leon/Library/Android/sdk/tools/proguard/proguard-android.txt
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


#-optimizationpasses 7
#-dontskipnonpubliclibraryclassmembers
#-printmapping proguardMapping.txt
#-optimizations !code/simplification/cast,!field/*,!class/merging/*
#-keepattributes *Annotation*,InnerClasses
#-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
#-dontoptimize
#-dontusemixedcaseclassnames
#-verbose
#-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers
#-dontwarn dalvik.**
#
##忽略警告
#-ignorewarnings
##记录生成的日志数据,gradle build时在本项目根目录输出
##apk 包内所有 class 的内部结构
#-dump class_files.txt
##未混淆的类和成员
#-printseeds seeds.txt
##列出从 apk 中删除的代码
#-printusage unused.txt
##混淆前后的映射
#-printmapping mapping.txt
#
#-keepattributes EnclosingMethod
#
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Fragment
#-keep public class * extends android.support.v4.app.Fragment
#-keep public class * extends android.app.Application
#-keep public class * extends android.support.multidex.MultiDexApplication
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class * extends android.view.View
#-keep public class com.android.vending.licensing.ILicensingService
#-keep class android.support.** {*;}
#
#-keep public class * extends android.view.View{
#    *** get*();
#    void set*(***);
#    public <init>(android.content.Context);
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
##这个主要是在layout 中写的onclick方法android:onclick="onMyClick"，不进行混淆
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#-keep class **.R$* {
# *;
#}
#
#
#-keepclassmembers class * {
#    void *(*Event);
#}
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
##// natvie 方法不混淆
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
##保持 Parcelable 不被混淆
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}

##----------------------------------------------------------------------------
#
##---------------------------------webview------------------------------------
#-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
#   public *;
#}
#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
#    public boolean *(android.webkit.WebView, java.lang.String);
#}
#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, jav.lang.String);
#}
#
##---------------------------------实体类---------------------------------
#
##下面中括号的地方需要要填你的包名
#-keep public class com.centaurstech.qiwu.R$*{
#    public static final int *;
#}
#-keepclassmembers class * {
#   public <init> (org.json.JSONObject);
#}
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
##*******************glide***************
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.AppGlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#
#
##google集成
#-keep class com.google.android.gms.** { *; }
#-dontwarn com.google.android.gms.**
#
## support-iv_audio_volume4
#-dontwarn android.support.v4.**
#-keep class android.support.v4.app.** { *; }
#-keep interface android.support.v4.app.** { *; }
#-keep class android.support.v4.** { *; }
#
## support design
#-dontwarn android.support.design.**
#-keep class android.support.design.** { *; }
#-keep interface android.support.design.** { *; }
#-keep public class android.support.design.R$* { *; }
#
## support-iv_audio_volume7
#-dontwarn android.support.v7.**
#-keep class android.support.v7.internal.** { *; }
#-keep interface android.support.v7.internal.** { *; }
#-keep class android.support.v7.** { *; }
#
##**************eventbus3.0************
#-keepattributes *Annotation*
#-keepclassmembers class ** {
#    @org.greenrobot.eventbus.Subscribe <methods>;
#}
#-keep enum org.greenrobot.eventbus.ThreadMode { *; }
#-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}
#-keepclassmembers class ** {
#    public void onEvent*(**);
#}
#-keepclassmembers class ** {
#public void xxxxxx(**);
#}
#
##*************BaseRecyclerViewAdapterHelper*************
#-keep class com.chad.library.adapter.** {
#*;
#}
#-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
#-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
#-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
#     <init>(android.view.View);
#}
#
##*************gson*****************
#-keep class com.google.gson.** {*;}
#-keep class com.google.**{*;}
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
#-keep class com.google.gson.examples.android.model.** { *; }
#
##AVLoadingIndicatorView
#-keep class com.wang.avi.** { *; }
#-keep class com.wang.avi.indicators.** { *; }
#
#
##zbar
#-keep class net.sourceforge.zbar.** { *; }
#-keep interface net.sourceforge.zbar.** { *; }
#-dontwarn net.sourceforge.zbar.**
#
##okgo
#-dontwarn com.lzy.okgo.**
#-keep class com.lzy.okgo.**{*;}
#
##okhttp
#-dontwarn okhttp3.**
#-keep class okhttp3.**{*;}
#
##okio
#-dontwarn okio.**
#-keep class okio.**{*;}
#
##okrx
#-dontwarn com.lzy.okrx.**
#-keep class com.lzy.okrx.**{*;}
#
##okrx2
#-dontwarn com.lzy.okrx2.**
#-keep class com.lzy.okrx2.**{*;}
#
##okserver
#-dontwarn com.lzy.okserver.**
#-keep class com.lzy.okserver.**{*;}
#
##fastJson
#-keepattributes Signature
#-dontwarn com.alibaba.fastjson.**
#-keep class com.alibaba.fastjson.**{*; }
#
##jpush
#-dontoptimize
#-dontpreverify
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }
#-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
#
#-dontwarn cn.jiguang.**
#-keep class cn.jiguang.** { *; }
#
#-dontwarn com.google.**
#-keep class com.google.gson.** {*;}
#-keep class com.google.protobuf.** {*;}
#
#
## Fresco
#-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
#
## Do not strip any method/class that is annotated with @DoNotStrip
#-keep @com.facebook.common.internal.DoNotStrip class *
#-keepclassmembers class * {
#    @com.facebook.common.internal.DoNotStrip *;
#}
#
## Keep native methods
#-keepclassmembers class * {
#    native <methods>;
#}
#-dontwarn javax.annotation.**
#-dontwarn com.android.volley.toolbox.**
#-dontwarn com.facebook.infer.**
#
#
##------------------  下方是android平台自带的排除项，这里不要动         ----------------
#
#-keep public class * extends android.app.Activity{
#	public <fields>;
#	public <methods>;
#}
#-keep public class * extends android.app.Application{
#	public <fields>;
#	public <methods>;
#}
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#
#
#-keepattributes *Annotation*
#
##------------------  下方是共性的排除项目         ----------------
## 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
## 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除
#
#-keepclasseswithmembers class * {
#    ... *JNI*(...);
#}
#
#-keepclasseswithmembernames class * {
#	... *JRI*(...);
#}
#
#-keep class **JNI* {*;}
#
#
##easyar
#-dontwarn cn.easyar.**
#-keep class cn.easyar.**{*;}
#-keep class cn.easyar.engine.**{*;}
#-keep class com.umeng.analytics.**{*;}
#
##阿里云
#-dontwarn com.alibaba.sdk.android.**
#-keep class com.alibaba.sdk.android.**{*;}
#
##Unity
#-keep class com.unity3d.player.**{*;}
#-keep class bitter.jnibridge.**{*;}
#-keep class org.fmod.**{*;}
#
##recyclerviewpager
#-keep class com.lsjwzh.widget.recyclerviewpager.**
#-dontwarn com.lsjwzh.widget.recyclerviewpager.**
#
#-dontwarn com.yanzhenjie.recyclerview.swipe.**
#-keep class com.yanzhenjie.recyclerview.swipe.** {*;}
#
#-dontwarn com.android.baselibrary.**
#-keep class com.android.baselibrary.** {*;}
#
##百度语音识别
#-keep class com.baidu.speech.**{*;}
#-keep class com.baidu.tts.**{*;}
#-keep class com.baidu.speechsynthesizer.**{*;}
#
##bugly
#-dontwarn com.tencent.bugly.**
#-keep public class com.tencent.bugly.**{*;}
#
##QiWu SDK
#-keep class com.centaurstech.qiwu.**{*;}
#-keep class com.centaurstech.voice.**{*;}
#
##高德
#
#-keep class com.amap.api.maps.**{*;}
#-keep class com.autonavi.**{*;}
#-keep class com.amap.api.trace.**{*;}
#
#-keep class com.amap.api.location.**{*;}
#-keep class com.amap.api.fence.**{*;}
#-keep class com.autonavi.aps.amapapi.model.**{*;}
#
#-keep class com.amap.api.services.**{*;}
#
#-keep class com.amap.api.maps2d.**{*;}
#-keep class com.amap.api.mapcore2d.**{*;}
#
#-keep class com.amap.api.navi.**{*;}
#-keep class com.autonavi.**{*;}

#-keep interface com.ximalaya.ting.android.opensdk.** {*;}
#-keep class com.ximalaya.ting.android.opensdk.** { *; }
#
#-dontwarn me.imid.swipebacklayout.lib.**
#-keep class me.imid.swipebacklayout.lib.**{*;}
#
##讯飞
#-keepattributes *Annotation*,InnerClasses
#-keepattributes Signature
#-dontwarn com.iflytek.**
#-keep class com.iflytek.** {*;}
#
##腾讯
#-dontwarn com.tencent.mm.**
#-keep class com.tencent.mm.**{*;}

#-keep class com.aprilbrother.aprilbrothersdk.**{*;}
#
#-keep class com.amap.api.**{*;}
#-keep class com.autonavi.**{*;}


-dontpreverify
-repackageclasses ''
-allowaccessmodification
# 不优化算法指令
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

# keep继承自系统组件的类
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# keep自定义view及其构造方法、set方法
-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# keep javascript注释的方法，使用到webview js回调方法的需要添加此配置
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}