[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MultiThreadDownloader-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1865)

***

# Android Multi-Thread Downloader
For more help please read [wiki](https://github.com/AigeStudio/MultiThreadDownloader/wiki).

## Android API Require
**API 1**

## Versions
### 1.0.0 release
* Multi-thread http download

### 1.2.1 release
* Bugfix:download thread dispath
* Support url redirection
* DLManager will download with single thread if server does not support break-point, and it will not insert to database

### 1.3.7 release
* Bugfix:can not start multi-threads to download file when we in url redirection.
* Bugfix:can not stop a download task when we in url redirection.

### 1.4.0 release
* Fix known bug.

### 1.4.1 release
* BugFix:Can not resume download after stopped with large file.
* Optimized code to enhancing the efficient implementation.
* Optimized thread dispatch.

### 1.4.2 release
* Add method getDLInfo(String url) to get the download info at the time.
* Add method getDLDBManager() to get the datebase manager.
* BugFix:Can not save progress when exception happened.

## Preview
**Download in activity**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview1.gif)

**Download in statusbar**

![](https://github.com/AigeStudio/MultiThreadDownloader/blob/master/preview2.gif)

***

# LICENSE
Copyright 2014-2015 [AigeStudio](https://github.com/AigeStudio), [zhangchi](https://github.com/kxdd2002)

Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.

You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
