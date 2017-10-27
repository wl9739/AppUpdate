
# AppUpdate

开发一款 APP 一般都会有一系列的版本迭代，因此国内很多 APP 开发都会需要下载更新和安装 APP 这个功能。鉴于此，用 Kotlin 写了这个库，便于开发。

> 只是常规的下载安装，并没有静默安装等黑科技



![](http://7xook5.com1.z0.glb.clouddn.com/update.gif)

## 功能

- [x] 适配 Android 7.+ 系统
- [x] RxJava + Retrofit 下载新版 apk 文件
- [x] RxBus 监听下载进度
- [x] 安装 apk 文件
- [x] 删除旧版 apk 文件

## 使用说明

> 由于没有找到一个比较好的上传 Kotlin 库到 Jcenter 的方案，暂时可以将 library 工程下的源码拷贝进自己的项目中使用 (拷贝的时候注意 res/xml 文件和 AndroidManifest.xml 文件中的内容)。
>
> 如果有比较好的上传 Kotlin 库的方案，希望能提 issue 给我 ：）

1. 下载 APP：

```
AppUpdate.downloadApk(apkUrl, apkVersion).subscribe{ "Your code" }
```

2. 安装 APP:

```
AppUpdate.installApk(apkFile)
```

3. 删除之前的 APP：

```
AppUpdate.deleteOldApk()
```

4. 监听下载进度，返回的 current 表示当前已经下载的内容长度，total 表示下载的 APK 的总大小：

```
AppUpdate.monitorDownloadProgress()
    .subscribe {progressBar.progress = (it.current * 100 / it.total).toInt()}
```

5. 通常，使用 RxJava 可以将下载和安装写在同一个调用链中：

```
AppUpdate.downloadApk(apkUrl, apkVersion).subscribe { AppUpdate.installApk(it) }
```


License
-------

    Copyright 2017 Qiushui

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.