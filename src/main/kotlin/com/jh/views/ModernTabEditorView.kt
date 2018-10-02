package com.jh.views

import com.jh.Workspace
import com.jh.smaliStructs.SmaliClass
import com.jh.smaliStructs.SmaliMethod
import com.jh.util.findTextAndShowDialog
import com.jh.util.loadPicture
import com.jh.util.logger
import javafx.scene.control.ListView
import javafx.scene.control.SplitPane
import javafx.scene.image.ImageView
import tornadofx.*
import java.io.File

class ModernTabEditorView(var currentFile: File = File("tmp.tmp")) : View("My View") {

    private val methodImage = loadPicture("/icons/method_icon.png")
    private val constructorImage = loadPicture("/icons/constructor_icon.png")

    override val root: SplitPane by fxml("/views/ModernTabEditorView.fxml")
    val editorWindowView: EditorWindowView by fxid("code_area")
    val methodsNamesListView: ListView<SmaliMethod> by fxid("methods_list")

    lateinit var smaliClass: SmaliClass
    private val methodsPopupMenu = contextmenu {
        item("Goto declaration")
        item("Find usages") {
            setOnAction {
                val selected = methodsNamesListView.selectedItem ?: return@setOnAction
                var combinedParams = selected.parameters.joinToString(";")
                if (combinedParams.isNotEmpty())
                    combinedParams+=";"
                val combinedName = "L${smaliClass.packageName.replace(".", "/")}/${smaliClass.name};->${selected.name}($combinedParams)"
                logger.info(combinedName)
                findTextAndShowDialog(combinedName)
            }
        }
        item("Rename")
    }

    init {

        if (!currentFile.exists()) ///REMOVE LATER
            currentFile.createNewFile()


        val clazz = Workspace.loadedClasses.find { smaliClass -> smaliClass.file == currentFile }
        clazz?.let { smaliClass = it }
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
                jumpToLine(selectedMethod.declarationLine)
            }
        }
        methodsNamesListView.contextMenu = methodsPopupMenu
    }

    fun jumpToLine(line: Int) {
        editorWindowView.moveTo(editorWindowView.position(line, 0).toOffset())
        editorWindowView.requestFollowCaret() //TODO more correct jump. (jump to line and scroll +-some lines)
        editorWindowView.requestFocus()


    }

}

