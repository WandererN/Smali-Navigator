package com.jh

import java.util.regex.Pattern

object HighlightsLoader {
    private fun loadFromResource(resourcePath: String): List<String> {
        val res = ArrayList<String>()
        res.addAll(java.io.FileReader(java.io.File(javaClass.getResource(resourcePath).file)).readLines())
        return res
    }

    private fun generateGroupPatternString(groupName: String, validNames: List<String>, startWith:String="",endWith: String = ""): String {
        return "($startWith(?<$groupName>(${validNames.joinToString("|")}))$endWith)"
    }

    fun generateCommentString() = generateGroupPatternString("COMMENT", listOf("#[^\\n]*"))
    fun generateLabelsString() =  generateGroupPatternString("LABEL", listOf("\\:[^\\s]*"),startWith = "(\\s+|^)")
    fun generateLineNamString() = generateGroupPatternString("LINENUMBER", listOf(".line[^\n]*"))
    fun generateStringString() = generateGroupPatternString("STRING", listOf("\".*\""))
    fun generateClassAndTypesString() = generateGroupPatternString("CLASSORTYPE", listOf("L[^;]*"),startWith = "(\\s+|\\(|:|;)")
    fun generateRegistersString() = generateGroupPatternString("REGISTER", listOf("(v|p)\\d"))
    fun generateOpcodesString() = generateGroupPatternString("OPCODE",
            loadFromResource("/highlights/opcodes.txt"), endWith = "(\\s+|$)")

    fun generateKeywordsString() = generateGroupPatternString("KEYWORD",
            loadFromResource("/highlights/keywords.txt"), endWith = "(\\s+|$)")

    fun generateGlobalPattern(vararg args: String): Pattern = Pattern.compile(args.joinToString("|"))

}