package com.jh.smaliStructs

import java.io.File

//TODO cover with tests
class SmaliClass : SmaliObject() {
    var sourceName: String =""
    var isPackage = false
    var file: File? = null
    lateinit var packageName: String
    lateinit var parent: String
    var interfacesNames = ArrayList<String>()
    var fields = ArrayList<SmaliClassField>()
    var methods = ArrayList<SmaliMethod>()
    override fun makeInfoString(): String {
        return """
            SmaliClass:
            ${super.makeInfoString()}
            packageName: $packageName
            isPackage: $isPackage
            sourceName: $sourceName
            parent: $parent
            methods: ${methods.joinToString(";") {it.name}}
            fields: ${fields.joinToString (";"){"${it.name}:${it.type}:${it.constValue}"}}
            interfaces: ${interfacesNames.joinToString(";")}
            """
    }
    override fun parseName(text: String) {
        val parsedFullClassName = text
                .split(" ")
                .last()
                .replace(";", "")
                .replaceFirst("L", "")
        name = parsedFullClassName
                .split("/")
                .last()
        packageName = parsedFullClassName
                .removeSuffix("/$name")
                .replace("/", ".")

    }

    private fun parseTwoWordLine(line: String): String {
        return line
                .split(" ")
                .last()
                .replace(";", "")
                .replaceFirst("L", "")
                .replace("/",".")
    }

    fun parseSuperLine(line: String) {
        parent = parseTwoWordLine(line)
    }

    fun parseImplementsLine(line: String) {
        interfacesNames.add(parseTwoWordLine(line))
    }

    fun parseMethodLine(line: String, lineNumber: Int) {
        methods.add(SmaliMethod.parseMethod(line, lineNumber))
    }

    fun parseFieldLine(line: String) {
        fields.add(SmaliClassField.parse(line))
    }

    fun parseSourceNameLine(line: String) {
        sourceName = line.split("\"").takeLast(2)[0]
    }

    fun parseClassLine(line: String) {
        parseName(line)
        parseModifiers(line)
    }

//    fun parsePackageName(projectRoot: File) {
//        packageName = file.toRelativeString(projectRoot)
//                .replace(File.separator, ".")
//                .replaceFirst(Regex("\\w+\\."), "")
//                .removeSuffix(".smali")//remove smali or smali_classes\\d
//    }

    companion object {
        fun parse(text: List<String>, file: File, projectRoot: File): SmaliClass {
            val res = SmaliClass()
            res.file = file
           // res.parsePackageName(projectRoot)
            with(res)
            {
                for ((lineNumber, line) in text.withIndex())
                    when {
                        line.matches(Regex("\\.class.*")) -> parseClassLine(line)
                        line.matches(Regex("\\.super.*")) -> parseSuperLine(line)
                        line.matches(Regex("\\.implements.*")) -> parseImplementsLine(line)
                        line.matches(Regex("\\.field.*")) -> parseFieldLine(line)
                        line.matches(Regex("\\.method.*")) -> parseMethodLine(line, lineNumber)
                        line.matches(Regex("\\.source.*")) -> parseSourceNameLine(line)
                    }
            }
            return res
        }
    }
}