package me.qiushui.update

import android.app.Application
import android.util.Log

/**
 * 获取 Application
 *
 * Created by Qiushui on 2017/10/21.
 */
internal object ApplicationUtil {

    val sApplication: Application

    init {
        var application: Application? = null
        try {
            application = Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null) as Application
        } catch (e: Exception) {
            Log.e(ApplicationUtil::class.java.simpleName, "Failed to get current application from AppGlobals." + e.message)
            try {
                application = Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null) as Application
            } catch (ex: Exception) {
                Log.e(ApplicationUtil::class.java.simpleName, "Failed to get current application from ActivityThread." + e.message)
            }
        } finally {
            sApplication = application!!
        }
    }

}