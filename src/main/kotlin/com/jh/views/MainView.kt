package com.jh.views

import com.jh.controllers.MainViewController
import com.jh.smaliStructs.SmaliClass
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TabPane
import javafx.scene.control.TreeView
import tornadofx.*
import tornadofx.FX.Companion.messages
import java.io.File

class MainView : View(messages["app_title"]) {
    private val controller: MainViewController by inject()
    lateinit var filesTreeView: TreeView<File>
    lateinit var packageTreeView: TreeView<SmaliClass>
    lateinit var sourcesTabs: TabPane
    private val mainMenu = menubar {
        menu(messages["file_menu_caption"])
        {
            item(messages["open_apktool_dir_menu_caption"]).action { controller.openMenuHandler(this@MainView) }
            item(messages["decompile_and_open_menu_caption"]).action { controller.decompileMenuHandler() }
            item(messages["compile_apk_menu_caption"]).action { controller.compileMenuHandler() }
            separator()
            item(messages["exit_menu_caption"]).action { close() }
        }
        menu(messages["view_menu_caption"])
        {
            menu(messages["search_menu_caption"])
            {
                item(messages["search_in_file_caption"])
                item(messages["search_in_project_caption"])
            }
        }
        menu(messages["help_menu_caption"])
        {
            item(messages["about_menu_caption"]) {
                action {
                    Alert(Alert.AlertType.INFORMATION, "Smali Application v.", ButtonType.OK).let {
                        it.title = "About..."
                        it.headerText = "About"
                        it.show()
                    }
                }
            }
        }
    }

    private val centerApp =
            splitpane {
                setDividerPositions(0.3)
                anchorpaneConstraints {
                    tabpane {
                        tab("Project") {
                            filesTreeView = treeview()
                        }
                        tab("Package") {
                            packageTreeView = treeview()
                        }
                    }
                }
                anchorpaneConstraints {
                    sourcesTabs = tabpane {
                    }
                }
            }

    override val root = borderpane {
        minWidth = 500.0
        minHeight = 300.0
        top = mainMenu
        center = centerApp
    }

    init {
    }
}

