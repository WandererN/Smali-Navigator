package com.jh.smaliStructs

class SmaliMethod : SmaliObject() {
    lateinit var returnType: String
    lateinit var parameters: List<String>
    var declarationLine: Int = 0
    var isConstructor: Boolean = false
    var isSynthetic: Boolean = false
    var isBridge: Boolean = false
    var hasVarargs: Boolean = false

    companion object {
        fun parseMethod(line: String, lineNumber: Int): SmaliMethod {
            val method = SmaliMethod()
            with(method)
            {
                parseName(line)
                parameters = if (line.contains("("))
                    line
                            .split("(")[1]
                            .split(")")[0]
                            .split(";").dropLast(1)
                else ArrayList()

                returnType = line
                        .split(")")[1]
                        .replace(";", "")
                        .replace("\n", "")

                with(line.split(name)[0]) {
                    isBridge = contains("bridge")
                    isConstructor = contains("constructor")
                    isSynthetic = contains("synthetic")
                    hasVarargs = contains("varargs")
                    parseModifiers(this)
                }

                declarationLine = lineNumber
            }
            return method
        }
    }
}