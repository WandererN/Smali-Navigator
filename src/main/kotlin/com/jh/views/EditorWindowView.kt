package com.jh.views

import com.jh.controllers.EditorWindowViewController
import com.jh.editor.AdvancedLineNumberFactory
import com.jh.util.loadCss
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.util.concurrent.Executors

class EditorWindowView : CodeArea() {
    private val executor = Executors.newSingleThreadExecutor()!!

    init {
        stylesheets += loadCss("/css/smaliKeywords.css")
        EditorWindowViewController(this, executor)
        this.paragraphGraphicFactory = AdvancedLineNumberFactory()//LineNumberFactory.get(this)
    }
}

