package com.jh.smaliStructs

class SmaliClassField : SmaliObject() {
    lateinit var type: String
    lateinit var constValue: String
    var isTransient: Boolean = false
    override fun parseName(text: String) {
        name = text
                .split(":")
                .first()
                .split(" ")
                .last()
    }

    fun parseConstValue(text: String) {
        constValue = "" //probably not necessary
    }

    companion object {
        fun parse(line: String): SmaliClassField {
            val res = SmaliClassField()
            res.parseModifiers(line)
            res.parseName(line)
            with(line)
            {
                res.isTransient = contains("transient")
                res.type = split(":")
                        .last()
                        .split("=")
                        .first()
                        .replace(" ", "")
                        .replaceFirst("L", "")
                        .replace(";", "")
            }
            res.parseConstValue(line)
            return res
        }
    }
}

