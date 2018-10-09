package com.jh

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import tornadofx.*
import tornadofx.FX.Companion.messages

object AppConfig {
    const val PROJECT_ROOT = "."
    val CONST_APP_TITLE = messages["app_title"]
    var apkToolPath: String = "$PROJECT_ROOT/apktool.jar"

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