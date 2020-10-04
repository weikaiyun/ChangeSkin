package com.common.weikaiyun.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object SkinFileUtils {
    private fun getSkinDir(context: Context): String {
        val skinDir = File(getCacheDir(context), SKIN_DEPLOY_PATH)
        if (!skinDir.exists()) {
            skinDir.mkdirs()
        }
        return skinDir.absolutePath
    }

    private fun getCacheDir(context: Context): String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val cacheDir = context.externalCacheDir
            if (cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())) {
                return cacheDir.absolutePath
            }
        }
        return context.cacheDir.absolutePath
    }

    private fun copySkinFromAssets(context: Context, name: String): String {
        val skinPath = File(getSkinDir(context), name).absolutePath
        try {
            val inStream = context.assets.open(SKIN_DEPLOY_PATH + File.separator + name)
            val outStream: OutputStream = FileOutputStream(skinPath)
            var byteCount: Int
            val bytes = ByteArray(1024)
            while (inStream.read(bytes).also { byteCount = it } != -1) {
                outStream.write(bytes, 0, byteCount)
            }
            outStream.close()
            inStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return skinPath
    }

    fun getSkinPath(context: Context, skinName: String): String {
        return copySkinFromAssets(context, skinName)
    }
}