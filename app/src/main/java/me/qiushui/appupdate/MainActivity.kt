package me.qiushui.appupdate

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import me.qiushui.update.AppUpdate

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取权限
        RxPermissions(this)
                .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (!granted) {
                        Toast.makeText(MainActivity@ this, "抱歉权限不足，不能更新 APK", Toast.LENGTH_SHORT).show()
                        downloadBtn.isEnabled = false
                    }
                }

        // 监听下载进度
        AppUpdate.monitorDownloadProgress()
                .subscribe {
                    val progress = (it.current * 100L / it.total).toInt()
                    progressBar.progress = progress
                    progressTv.text = "$progress %"
                }

        downloadBtn.setOnClickListener {
            download()
        }
    }

    private fun download() {
        val testDownloadUrl = "http://oy6b4ovyd.bkt.clouddn.com/app-debug.apk"
        AppUpdate.downloadApk(testDownloadUrl, "1.0.0")
                .subscribe({
                    // 安装 APK
                    AppUpdate.installApk(it)
                }, {
                    // 异常处理
                })
    }
}
