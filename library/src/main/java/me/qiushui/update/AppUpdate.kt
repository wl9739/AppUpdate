package me.qiushui.update

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.qiushui.update.ApplicationUtil.sApplication
import java.io.File

/**
 * App 实现自动下载及更新的入口类
 * 使用方法：
 *
 * 1. 下载 APK:
 *      AppUpdate.downloadApk(apkUrl, apkVersion).subscribe{ "Your code" }
 *
 * 2. 安装 APK:
 *      AppUpdate.installApk(apkFile)
 *
 * 3. 删除之前的 APK:
 *      AppUpdate.deleteOldApk()
 *
 * 4. 监听下载进度，返回的 current 表示当前已经下载的内容长度，total 表示下载的 APK 的总大小：
 *
 *     AppUpdate.monitorDownloadProgress()
 *     .subscribe {progressBar.progress = (it.current * 100 / it.total).toInt()}
 *
 * 通常，使用 RxJava 可以将下载和更新写在同一个调用链中：
 *      AppUpdate.downloadApk(apkUrl, apkVersion).subscribe { AppUpdate.installApk(it) }
 *
 * Created by Qiushui on 2017/10/21.
 */
object AppUpdate {

    private val MIME_TYPE_APK = "application/vnd.android.package-archive"

    /**
     * 下载 APK
     * @param url       下载地址
     * @param version   版本号
     */
    fun downloadApk(url: String, version: String): Observable<File> =
            Observable.defer { Observable.just(DownloadApi.getDownloadApi().getUpdateApk(url).execute().body()?.byteStream()) }
                    .map { FileUtil.saveApk(it, version) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    /**
     * 监听下载进度
     */
    fun monitorDownloadProgress(): Observable<DownloadProgressMsg> =
            RxBus.listen(DownloadProgressMsg::class.java).observeOn(AndroidSchedulers.mainThread())

    /**
     * 安装 APK
     */
    fun installApk(apkFile: File) {
        val installApkIntent = Intent()
        installApkIntent.action = Intent.ACTION_VIEW
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT)
        installApkIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApkIntent.setDataAndType(FileProvider.getUriForFile(sApplication, getFileProviderAuthority(), apkFile), MIME_TYPE_APK)
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(apkFile), MIME_TYPE_APK)
        }

        if (sApplication.packageManager.queryIntentActivities(installApkIntent, 0).size > 0) {
            sApplication.startActivity(installApkIntent)
        }
    }

    /**
     * 删除之前升级时下载的老的 apk 文件
     */
    fun deleteOldApk() {
        val apkDir = FileUtil.getApkFileDir()
        apkDir.deleteRecursively()
    }

    /**
     * 获取安装权限
     */
    private fun getFileProviderAuthority(): String? {
        try {
            sApplication.packageManager.getPackageInfo(sApplication.packageName, PackageManager.GET_PROVIDERS).providers
                    .filter { FileProvider::class.java.name == it.name && it.authority.endsWith(".qiushui.provider") }
                    .forEach { return it.authority }
        } catch (ignore: PackageManager.NameNotFoundException) {
        }
        return null
    }

}
