package com.jh.views

import com.jh.SmaliAppLauncher
import com.jh.smaliStructs.SmaliMethod
import com.jh.smaliStructs.SmaliClass
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.fxmisc.flowless.VirtualizedScrollPane
import tornadofx.*
import java.io.File

class TabEditorView(var currentFile: File) : Tab(currentFile.name) {
    var editorWindowView = EditorWindowView()
    lateinit var smaliClass: SmaliClass
    var methodsNamesListView = ListView<SmaliMethod>()
    private val methodImage = Image(SmaliAppLauncher::class.java.getResourceAsStream("/icons/method_icon.png"))
    private val constructorImage = Image(SmaliAppLauncher::class.java.getResourceAsStream("/icons/constructor_icon.png"))
    private val methodsPopupMenu = contextmenu{
        item("Goto declaration")
        item("Find usages")
        item("Rename")
    }
    init {
        val clazz = com.jh.Workspace.loadedClasses.find { smaliClass -> smaliClass.file==currentFile }
        clazz?.let {
            smaliClass = it
        }
        val splitPane = SplitPane()
        val scrollPane = VirtualizedScrollPane(editorWindowView)
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        splitPane.add(scrollPane)
        methodsNamesListView.cellFormat { smaliMethod ->
            graphic = when {
                smaliMethod.isConstructor -> ImageView(constructorImage)
                else -> ImageView(methodImage)
            }
            val parameters = smaliMethod.parameters.joinToString(",")
            text = "${smaliMethod.name}($parameters):${smaliMethod.returnType}"
        }
        methodsNamesListView.setOnMouseClicked {
            if (it.clickCount == 2) {
                val selectedMethod = methodsNamesListView.selectionModel.selectedItem ?: return@setOnMouseClicked
                editorWindowView.moveTo(editorWindowView.position(selectedMethod.declarationLine, 0).toOffset())
                editorWindowView.requestFollowCaret() //TODO more correct jump. (jump to line and scroll +-some lines)
                editorWindowView.requestFocus()
            }
        }
        methodsNamesListView.contextMenu = methodsPopupMenu

        splitPane.add(methodsNamesListView)
        splitPane.setDividerPositions(0.8)
        add(splitPane)
    }
}