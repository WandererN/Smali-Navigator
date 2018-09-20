package com.jh.extToolLogger

import javafx.scene.control.TextArea

class TextBoxTextLogger(var textBox: TextArea) : ITextLogger {
    override fun logLine(text: String) {
        textBox.appendText( text + "\n")
    }
}