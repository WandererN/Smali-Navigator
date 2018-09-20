package com.jh

import com.jh.views.MainView
import tornadofx.*
import java.io.File

class SmaliAppLauncher : App(MainView::class) {
    companion object {
        private const val CONFIG_FILE_PATH = "debugroot/config.json"
        @JvmStatic
        fun main(args: Array<String>) {
            if (File(CONFIG_FILE_PATH).exists())
                AppConfig.inflateFromJson(File(CONFIG_FILE_PATH).readText())
            launch<SmaliAppLauncher>(args)
        }
    }
}