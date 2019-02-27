# android-iflytek

[![Build Status](https://cloud.drone.io/api/badges/v7lin/android-iflytek/status.svg)](https://cloud.drone.io/v7lin/android-iflytek)
[![GitHub tag](https://img.shields.io/github/tag/v7lin/android-iflytek.svg)](https://github.com/v7lin/android-iflytek/releases)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
### iflytek

````
科大讯飞有毒，居然将 SDK 和 APP_ID 绑定！！！
这将让二次封装变得没有意义！！！
````

````
APPID: 5c61057f
只开通了"在线语音合成"服务
````

### snapshot

````
ext {
    latestVersion = '1.0.0-SNAPSHOT'
}

allprojects {
    repositories {
        ...
        maven {
            url 'https://oss.jfrog.org/artifactory/oss-snapshot-local'
        }
        ...
    }
}
````

### release

````
ext {
    latestVersion = '1.0.0'
}

allprojects {
    repositories {
        ...
        jcenter()
        ...
    }
}
````

### usage

android
````
...
dependencies {
    ...
    implementation "io.github.v7lin:iflytek-android:${latestVersion}"
    ...
}
...
````

### example

[android example](./app/src/main/java/io/github/v7lin/iflytek/MainActivity.java)
