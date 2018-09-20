package com.jh.views

import com.jh.SmaliAppLauncher
import com.jh.controllers.EditorWindowViewController
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.util.concurrent.Executors

class EditorWindowView : CodeArea() {
    private val executor = Executors.newSingleThreadExecutor()!!

    init {
        stylesheets += SmaliAppLauncher::class.java.getResource("/css/smaliKeywords.css").toExternalForm()
        EditorWindowViewController(this, executor)
        this.paragraphGraphicFactory = LineNumberFactory.get(this)
    }
}

