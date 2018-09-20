package com.jh.util

import com.jh.extToolLogger.ITextLogger
import java.util.*

fun String.executeInShell(textLogger: ITextLogger?) {
    val thread = Thread {
        val proc = Runtime.getRuntime().exec("cmd /c $this")
        var hasNext = true
        val inStreamScanner = Scanner(proc.inputStream)
        val errStreamScanner = Scanner(proc.errorStream)
        while (hasNext) {
            if (inStreamScanner.hasNext())
                textLogger?.logLine(inStreamScanner.nextLine())

            if (errStreamScanner.hasNext())
                textLogger?.logLine(errStreamScanner.nextLine())

            hasNext = inStreamScanner.hasNext() || errStreamScanner.hasNext()
        }
        textLogger?.logLine("\nFinished with code ${proc.exitValue()}")
    }
    thread.start()
}
