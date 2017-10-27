package me.qiushui.update

import me.qiushui.update.ApplicationUtil.sApplication
import java.io.File
import java.io.InputStream

/**
 * 文件工具
 *
 * Created by Qiushui on 2017/10/21.
 */
internal object FileUtil {
    private val DIR_NAME_APK = "update_apk"

    /**
     * 获取 apk 文件夹
     */
    fun getApkFileDir(): File = sApplication.getExternalFilesDir(DIR_NAME_APK)

    /**
     * 获取 apk 文件
     */
    private fun getApkFile(version: String): File {
        val appName: String = try {
            sApplication.packageManager.getPackageInfo(sApplication.packageName, 0).applicationInfo
                    .loadLabel(sApplication.packageManager).toString()
        } catch (ignore: Exception) {
            ""
        }
        return File(getApkFileDir(), appName + "_v" + version + ".apk")
    }

    /**
     * 保存 apk 文件
     */
    fun saveApk(input: InputStream, version: String) = getApkFile(version).apply { copyInputStreamToFile(input) }

    /**
     * 写文件
     */
    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }
}