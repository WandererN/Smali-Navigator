package com.jh

import java.util.regex.Pattern

object HighlightsLoader {
    private fun loadFromResource(resourcePath: String): List<String> {
        val res = ArrayList<String>()
        res.addAll(java.io.FileReader(java.io.File(javaClass.getResource(resourcePath).file)).readLines())
        return res
    }

    private fun generateGroupPatternString(groupName: String, validNames: List<String>, endWith: String = ""): String {
        return "(?<$groupName>(${validNames.joinToString("|")}))$endWith"
    }

    fun generateCommentString() = generateGroupPatternString("COMMENT", listOf("#[^\\n]*"))
    fun generateLabelsString() = "(\\s*|^)" + generateGroupPatternString("LABEL", listOf("\\:[^\\s]*"))
    fun generateLineNamString() = generateGroupPatternString("LINENUMBER", listOf(".line[^\n]*"))
    fun generateStringString() = generateGroupPatternString("STRING", listOf("\".*\""))
    fun generateClassAndTypesString() = generateGroupPatternString("CLASSORTYPE", listOf("L[^;]*"))
    fun generateRegistersString() = generateGroupPatternString("REGISTER", listOf("(v|p)\\d"))
    fun generateGlobalPattern(vararg args: String): Pattern = Pattern.compile(args.joinToString("|"))
    fun generateOpcodesString() = generateGroupPatternString("OPCODE",
            loadFromResource("/highlights/opcodes.txt"), "(\\s|$)")

    fun generateKeywordsString() = generateGroupPatternString("KEYWORD",
            loadFromResource("/highlights/keywords.txt"), "(\\s|$)")

}