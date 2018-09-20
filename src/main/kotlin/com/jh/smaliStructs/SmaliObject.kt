package com.jh.smaliStructs

open class SmaliObject {
    lateinit var name: String
    var accessModifier: SmaliAccessModifier = SmaliAccessModifier.PRIVATE
    var isFinal: Boolean = false
    var isStatic: Boolean = false
    var isAbstract: Boolean = false
    var isVolatile: Boolean = false
    var isSynchronized: Boolean = false

    protected open fun parseName(text: String) {
        name = text.split("(")[0].split(" ").last()
    }

    protected fun parseModifiers(text: String) {
        with(text)
        {
            isStatic = contains("static")
            isFinal = contains("final")
            isAbstract = contains("abstract")
            isVolatile = contains("volatile")
            isSynchronized = contains("synchronized")

            accessModifier = when {
                contains("public") -> SmaliAccessModifier.PUBLIC
                contains("private") -> SmaliAccessModifier.PRIVATE
                contains("protected") -> SmaliAccessModifier.PROTECTED
                else -> SmaliAccessModifier.PRIVATE
            }

        }

    }
}