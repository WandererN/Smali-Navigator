package com.jh

import com.jh.extToolLogger.ITextLogger
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

object AppConfig {
    const val PROJECT_ROOT = "debugRoot"
    const val CONST_APP_TITLE = "Smali navigator "
    var apkToolPath: String = "$PROJECT_ROOT\\apktool.jar"
    var extUtilsLogger: ITextLogger? = null
    fun inflateFromJson(jsonText: String) {
        val parsedJSON = JSONParser().parse(jsonText) as JSONObject
        apkToolPath = parsedJSON["apktool_path"] as String
    }

    fun exportConfigToJson() {
        val exportScheme = hashMapOf(
                "apktool_path" to apkToolPath
        )
        JSONObject(exportScheme).toJSONString()

    }

}