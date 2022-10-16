package com.example.myapplication

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {

    private val packageName = "com.miracle.drawtosave.an"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        if (packageName == lpparam.packageName) {
            xlog(TAG + "Hook an app start:$packageName")

            hookClearAds(lpparam)
        }
    }

    private fun hookClearAds(lpparam: XC_LoadPackage.LoadPackageParam) {
        val googleBillingUtilClass = XposedHelpers.findClass("org.cocos2dx.javascript.GoogleBillingUtil",lpparam.classLoader)
        val appActivityClass = lpparam.classLoader.loadClass("org.cocos2dx.javascript.AppActivity")
        XposedHelpers.findAndHookMethod(appActivityClass, "initSdk", appActivityClass,object : XC_MethodHook() {

            override fun afterHookedMethod(param: MethodHookParam?) {
                xlog("initSdk（）运行完了，准备关闭广告-----------")
                XposedHelpers.callStaticMethod(googleBillingUtilClass,"confirmPurchaseCallback")
            }
        })

        val rewardAdCallClass = XposedHelpers.findClass("org.cocos2dx.javascript.RewardAdCall",lpparam.classLoader)
        val bridge = XposedHelpers.findClass(
            "org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge",
            lpparam.classLoader
        )
        XposedHelpers.findAndHookMethod(rewardAdCallClass,"showVideo",object: XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                XposedHelpers.callStaticMethod(bridge,"evalString","window.videoFinish();")
                return ""
            }
        })


    }
}

const val TAG = "MainHook->"

fun xlog(string: String) {
    XposedBridge.log(TAG + string)
}
