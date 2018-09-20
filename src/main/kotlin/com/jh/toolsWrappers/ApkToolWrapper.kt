package com.jh.toolsWrappers

import com.jh.AppConfig
import com.jh.util.executeInShell


object ApkToolWrapper {
    fun compile(pathToDir: String, targetApk: String) {
        "java -jar ${AppConfig.apkToolPath} b $pathToDir -f -o $targetApk".executeInShell(AppConfig.extUtilsLogger)
    }

    fun decompile(pathToApk: String, targetPath: String) {
        "java -jar ${AppConfig.apkToolPath} d $pathToApk -f -o $targetPath".executeInShell(AppConfig.extUtilsLogger)
    }
}