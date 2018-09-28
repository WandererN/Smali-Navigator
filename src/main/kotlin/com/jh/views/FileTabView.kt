package com.jh.views

import javafx.scene.control.Tab
import tornadofx.*
import java.io.File

class FileTabView(val file: File) : Tab(file.name) {
    val content = ModernTabEditorView(file)

    init {
        add(content.root)
    }
}