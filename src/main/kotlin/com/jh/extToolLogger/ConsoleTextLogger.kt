package com.jh.extToolLogger

class ConsoleTextLogger : ITextLogger {
    override fun logLine(text: String) {
        println(text)
    }
}