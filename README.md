*You can jump to EN README after CN if you don't read chinese.*
***
**Here is begin of CN document.**
# Andorid多线程断点续传下载器
逻辑比较简单但实用的Android多线程断点续传下载器
##Android API 版本要求
**API 1**
##版本迭代
###1.0.0 Release
多线程断点续传暂未发现问题
##预览图
**在普通界面中下载**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview1.gif)

**在状态栏中下载**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview2.gif)
##功能简介
* 多线程下载
* 断点续传

更多的功能方法可查看[DLManager.java](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/Downloader/src/main/java/cn/aigestudio/downloader/bizs/DLManager.java)
##如何集成到项目
###步骤一
将Downloader这个Module导入你的Project中
###步骤二
在你Project的settings.gradle文件中增加如下内容：
```gradle
include ':Downloader'
```
这里要注意的是在一些gradle版本中需要以英文逗号的方式追加Module：
```gradle
include ':YourMoudle',':Downloader'
```
添加后当出现“sycn now”提示时点击同步即可
###步骤三
在你项目的build.gradle文件的dependencies区域中添加如下内容：
```gradle
compile project(':Downloader')
```
##如何使用
一旦将Downloader集成到项目后你便可以调用DLManager中的dlStart、dlStop和dlCancel方法来开始、停止和取消一个下载任务：

**开始一个下载任务**
```Java
DLManager.getInstance(context).dlStart(url, dirPath, null);
```
如果你需要对下载过程进行监听，可为dlStart指定一个DLTaskListener监听器：
```Java
DLManager.getInstance(context).dlStart(url, dirPath,
        new DLTaskListener() {
            @Override
            public void onProgress(int progress) {
                // 下载进行时
            }
        });
```
关于监听器支持的具体方法请查看[DLTaskListener.java](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/Downloader/src/main/java/cn/aigestudio/downloader/interfaces/DLTaskListener.java)

**停止一个下载任务**
```Java
DLManager.getInstance(context).dlStop(url);
```
停止一个下载任务很简单，只需要传入url即可

**取消一个下载任务**
```Java
DLManager.getInstance(context).dlCancel(url);
```
同样地，取消一个下载任务很简单，也只需要传入url即可，取消和停止一个下载任务两者唯一的不同是前者会删除掉该次任务在数据库中所有的相关数据而停止则不会。如果你停止了一次下载任务，那么如果下次再次传入相同的url则会继续从上次停止时的位置开始下载。注意：**一次下载任务的唯一标识是url**。

更多相关的使用请参考Demo

***
**这里开始是英文文档**
# Downloader for Android multi-thread http download
Easy and useful downloader for Android
##Version
###1.0.0 Release
Multi-thread http download
##Preview
**Download in activity**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview1.gif)

**Download in statusbar**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview2.gif)
##Function
* Multi-thread
* Resume broken downloads

You can see [DLManager.java](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/Downloader/src/main/java/cn/aigestudio/downloader/bizs/DLManager.java) for more help.
##How to add to your project
###step 1
import Downloaderlib to your project
###step 2
Add something like below in your settings.gradle file of project:
```gradle
include ':Downloader'
```
Note that in some other gradle version you many add module like below:
```gradle
include ':YourMoudle',':Downloader'
```
Click 'sycn now' when it appear after module add.
###step 3
Add something like below in your build.gradle file of project:
```gradle
compile project(':Downloader')
```
##Usage
Once you add DatePicker to your project you can use method dlStart dlStop and dlCancel provide by DLManager to start stop and cancel a download task:

**Start a download task**
```Java
DLManager.getInstance(context).dlStart(url, dirPath, null);
```
You can set a DLTaskListener for dlStart if you want to monitor download process:
```Java
DLManager.getInstance(context).dlStart(url, dirPath,
        new DLTaskListener() {
            @Override
            public void onProgress(int progress) {
                // when download progressing
            }
        });
```
You can see [DLTaskListener.java](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/Downloader/src/main/java/cn/aigestudio/downloader/interfaces/DLTaskListener.java) for more about download listener.

**Stop a download task**
```Java
DLManager.getInstance(context).dlStop(url);
```
Just use dlStop method and transfer url to stop a download task.

**Cancel a download task**
```Java
DLManager.getInstance(context).dlCancel(url);
```
Similarly, use dlCancelmethod and transfer url to cancel a download task. The difference between dlStop and dlCancel is whether the data in database would be deleted or not, for example, the state of download like local file and data in database will be save when you use dlStop stop a download task, if you use dlCancel cancel a download task, anything related to download task would be deleted. note:**The unique identification of a download task is url**.

More for usage please see Demo.

***
#LICENSE
 Copyright 2014-2015 AigeStudio(https://github.com/AigeStudio)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
