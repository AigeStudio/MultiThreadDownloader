[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MultiThreadDownloader-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1865)

*You can jump to EN README after CN if you don't read chinese.*

***

**Here is begin of CN document.**
# Android API 版本要求
**API 4**
# 版本迭代
## 1.0.0 release
* 多线程断点续传暂未发现问题

## 1.2.1 release
* 修复下载线程分配问题
* 支持域名地址重定向
* 如果服务器不支持断点下载则启用单线程下载且不存储于数据库

## 1.3.7 release
* 修复域名重定向后不能进行多线程下载的问题
* 修复域名重定向后不能停止下载任务的问题

## 1.4.0 release
* 修复已知Bug

## 1.4.1 release
* 修复打文件下载暂停后无法续传问题
* 优化线程分配
* 优化下载判定逻辑提升代码执行效率

# 预览图
**在普通界面中下载**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview1.gif)

**在状态栏中下载**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview2.gif)
# 功能简介
* 多线程下载
* 断点续传

更多的功能方法可查看[DLManager.java](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/Downloader/src/main/java/cn/aigestudio/downloader/bizs/DLManager.java)

# 如何集成到项目
## 方式一 直接从maven center compile
~~compile 'cn.aigestudio.downloader:Downloader:1.3.7'~~
```java
compile 'cn.aigestudio.downloader:Downloader:1.4.0'
```

## 方式二 自己动手
### 步骤一
将Downloader这个Module导入你的Project中

### 步骤二
在你Project的settings.gradle文件中增加如下内容：

```gradle
include ':Downloader'
```

这里要注意的是在一些gradle版本中需要以英文逗号的方式追加Module：

```gradle
include ':YourMoudle',':Downloader'
```

添加后当出现“sycn now”提示时点击同步即可

### 步骤三
在你项目的build.gradle文件的dependencies区域中添加如下内容：

```gradle
compile project(':Downloader')
```

***

**这里开始是英文文档**

# Android API Needs
**API 4**
# Versions
## 1.0.0 release
* Multi-thread http download

## 1.2.1 release
* Bugfix:download thread dispath
* Support url redirection
* DLManager will download with single thread if server does not support break-point, and it will not insert to database

## 1.3.7 release
* Bugfix:can not start multi-threads to download file when we in url redirection.
* Bugfix:can not stop a download task when we in url redirection.

## 1.4.0 release
* Fix known bug.

## 1.4.1 release
* BugFix:Can not resume download after stopped with large file.
* Optimized code to enhancing the efficient implementation.
* Optimized thread dispatch.

# Preview
**Download in activity**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview1.gif)

**Download in statusbar**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview2.gif)

# Function
* Multi-thread
* Resume broken downloads

You can see [DLManager.java](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/Downloader/src/main/java/cn/aigestudio/downloader/bizs/DLManager.java) for more help.

# How to add to your project
## Method A:compile from maven center
```java
compile 'cn.aigestudio.downloader:Downloader:1.3.7'
```

## Method B:Help yourself
### step 1
import Downloaderlib to your project

### step 2
Add something like below in your settings.gradle file of project:

```gradle
include ':Downloader'
```

Note that in some other gradle version you many add module like below:

```gradle
include ':YourMoudle',':Downloader'
```

Click 'sycn now' when it appear after module add.

### step 3
Add something like below in your build.gradle file of project:

```gradle
compile project(':Downloader')
```

***

# LICENSE

Copyright 2014-2015 [AigeStudio](https://github.com/AigeStudio), [zhangchi](https://github.com/kxdd2002)

Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.

You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
