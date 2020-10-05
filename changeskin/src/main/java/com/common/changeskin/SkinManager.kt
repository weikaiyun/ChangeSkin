package com.common.changeskin

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.text.TextUtils
import android.view.View
import com.common.changeskin.attr.SkinAttrSupport
import com.common.changeskin.attr.SkinView
import com.common.changeskin.callback.ISkinChangingCallback
import com.common.changeskin.utils.L
import com.common.changeskin.utils.PrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

object SkinManager {
    private lateinit var mContext: Context
    private lateinit var mResourceManager: ResourceManager
    private lateinit var mPrefUtils: PrefUtils
    private var usePlugin = false
    private var mSuffix: String = ""
    private var mCurPluginPath: String = ""
    private var mCurPluginPkg: String = ""
    private val mActivities: MutableList<Activity> = ArrayList()

    fun init(context: Context) {
        mContext = context.applicationContext
        mPrefUtils = PrefUtils(mContext)
        val skinPluginPath = mPrefUtils.pluginPath
        val skinPluginPkg = mPrefUtils.pluginPkgName
        mSuffix = mPrefUtils.suffix
        if (!validPluginParams(skinPluginPath, skinPluginPkg)) return
        try {
            loadPlugin(skinPluginPath, skinPluginPkg)
            mCurPluginPath = skinPluginPath
            mCurPluginPkg = skinPluginPkg
        } catch (e: Exception) {
            mPrefUtils.clear()
            e.printStackTrace()
        }
    }

    private fun loadPlugin(skinPath: String, skinPkgName: String) {
        val packageInfo = getPackageInfo(skinPath)
        packageInfo?.apply {
            applicationInfo.sourceDir = skinPath
            applicationInfo.publicSourceDir = skinPath
            val skinRes: Resources = mContext.packageManager.getResourcesForApplication(applicationInfo)
            val oriRes = mContext.resources
            val resources = Resources(skinRes.assets, oriRes.displayMetrics, oriRes.configuration)
            mResourceManager = ResourceManager(resources, skinPkgName, "")
            usePlugin = true
        }
    }

    private fun validPluginParams(skinPath: String, skinPkgName: String): Boolean {
        if (skinPath.isEmpty() || skinPkgName.isEmpty()) {
            return false
        }
        val file = File(skinPath)
        if (!file.exists()) return false
        val info = getPackageInfo(skinPath) ?: return false
        return info.packageName == skinPkgName
    }

    private fun getPackageInfo(skinPluginPath: String): PackageInfo? {
        val pm = mContext.packageManager
        return pm.getPackageArchiveInfo(skinPluginPath, 0)
    }

    private fun checkPluginParamsThrow(skinPath: String, skinPkgName: String) {
        require(validPluginParams(skinPath, skinPkgName)) { "skinPluginPath or skinPkgName not valid ! " }
    }

    fun removeAnySkin() {
        L.e("removeAnySkin")
        clearPluginInfo()
        notifyChangedListeners()
    }

    fun needChangeSkin(): Boolean {
        return usePlugin || !TextUtils.isEmpty(mSuffix)
    }

    val resourceManager: ResourceManager
        get() {
            if (!usePlugin) {
                mResourceManager = ResourceManager(mContext.resources, mContext.packageName, mSuffix)
            }
            return mResourceManager
        }

    /**
     * 应用内换肤，传入区别资源的后缀
     */
    fun changeSkin(suffix: String) {
        clearPluginInfo()
        mSuffix = suffix
        mPrefUtils.putPluginSuffix(suffix)
        notifyChangedListeners()
    }

    private fun clearPluginInfo() {
        mCurPluginPath = ""
        mCurPluginPkg = ""
        usePlugin = false
        mSuffix = ""
        mPrefUtils.clear()
    }

    /**
     * 根据suffix选择插件内某套皮肤，默认为""
     */
    fun changeSkin(skinPluginPath: String, skinPluginPkg: String,
                   callback: ISkinChangingCallback = ISkinChangingCallback.DEFAULT_SKIN_CHANGING_CALLBACK) {
        L.e("changeSkin = $skinPluginPath , $skinPluginPkg")
        callback.onStart()
        try {
            checkPluginParamsThrow(skinPluginPath, skinPluginPkg)
        } catch (e: IllegalArgumentException) {
            callback.onError(RuntimeException("checkPlugin occur error"))
            return
        }

        GlobalScope.launch(Dispatchers.Default) {
            try {
                loadPlugin(skinPluginPath, skinPluginPkg)
                withContext(Dispatchers.Main) {
                    try {
                        updatePluginInfo(skinPluginPath, skinPluginPkg)
                        notifyChangedListeners()
                        callback.onComplete()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback.onError(e)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onError(e)
            }
        }
    }

    private fun updatePluginInfo(skinPluginPath: String, pkgName: String) {
        mPrefUtils.putPluginPath(skinPluginPath)
        mPrefUtils.putPluginPkgName(pkgName)
        mPrefUtils.putPluginSuffix("")
        mCurPluginPkg = pkgName
        mCurPluginPath = skinPluginPath
        mSuffix = ""
    }

    private fun notifyChangedListeners() {
        for (activity in mActivities) {
            apply(activity)
        }
    }

    fun apply(activity: Activity) {
        val skinViews = SkinAttrSupport.getSkinViews(activity)
        for (skinView in skinViews) {
            skinView.apply()
        }
    }

    fun register(activity: Activity) {
        mActivities.add(activity)
        activity.findViewById<View>(android.R.id.content)
            .post { apply(activity) }
    }

    fun unregister(activity: Activity) {
        mActivities.remove(activity)
    }

    /**
     * apply for dynamic construct view
     */
    fun injectSkin(view: View) {
        val skinViews: List<SkinView> = ArrayList()
        SkinAttrSupport.addSkinViews(view, skinViews)
        for (skinView in skinViews) {
            skinView.apply()
        }
    }
}