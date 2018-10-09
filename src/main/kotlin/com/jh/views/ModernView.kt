package com.jh.views

import com.jh.AppConfig
import com.jh.Workspace
import com.jh.logs.TextViewLoggerAppender
import com.jh.smaliStructs.SmaliClass
import com.jh.toolsWrappers.ApkToolWrapper
import com.jh.util.findSmaliClasssesWithTextInProject
import com.jh.util.findTextAndShowDialog
import com.jh.util.loadPicture
import com.jh.views.search.SearchResultsView
import com.jh.views.search.SearchTableDataClass
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.apache.logging.log4j.LogManager
import tornadofx.*
import tornadofx.FX.Companion.messages
import java.io.File
import kotlin.system.exitProcess

class ModernView : View(messages["app_title"]) {
    private val folderImage = loadPicture("/icons/folder_icon.png")
    private val smaliIcon = loadPicture("/icons/smali_file_icon.png")
    private val unknownIcon = loadPicture("/icons/unknown_file_icon.png")
    private val packageIcon = loadPicture("/icons/package_icon.png")

    override val root: BorderPane by fxml("/views/ModernView.fxml")
    private val tabPane: TabPane by fxid("sources_tab_pane")
    private val filesTreeView: TreeView<File> by fxid("project_tree_view")
    private val packageTreeView: TreeView<SmaliClass> by fxid("package_tree_view")
    private val compileMenu: MenuItem by fxid("compile_menu")
    private val closeMenu: MenuItem by fxid("close_menu")
    private val logTextArea: TextArea by fxid("log_textbox")
    private val logger = LogManager.getLogger(this.javaClass.name)

    init {
        TextViewLoggerAppender.loggingTextArea = logTextArea
        Workspace.mainView = this
    }

    private fun findOrCreatePackage(packageName: String, root: TreeItem<SmaliClass>): TreeItem<SmaliClass> {
        val pkgDirs = packageName.split(".")
        var currTreeItem = root
        for (pkg in pkgDirs) {
            var foundItem = currTreeItem.children.find { elem -> elem.value.name == pkg }
            if (foundItem == null || !foundItem.value.isPackage) {
                val newClass = SmaliClass()
                newClass.name = pkg
                newClass.isPackage = true
                foundItem = TreeItem(newClass)
                currTreeItem.children.add(foundItem)
            }

            currTreeItem = foundItem
        }
        return currTreeItem
    }

    private fun fillTreeView(root: TreeItem<File>, rootDir: File) {
        val files = rootDir.listFiles() ?: return
        files.sortBy { file -> !file.isDirectory }
        val filesList = ArrayList<TreeItem<File>>()
        files.forEach {
            val item = TreeItem(it)
            if (it.isDirectory)
                fillTreeView(item, it)
            filesList.add(item)
            if (it.isFile && it.extension == "smali") {
                Workspace.loadedClasses.add(SmaliClass.parse(it.readLines(), it))
            }
        }
        root.children.addAll(filesList)
    }

    private fun fillPackageView(root: TreeItem<SmaliClass>) {
        for (clazz in Workspace.loadedClasses) {
            val treeItem = findOrCreatePackage(clazz.packageName, root)
            treeItem.children.add(TreeItem(clazz))
        }
    }

    fun jumpToLine(line: Int) {
        (tabPane.tabs[tabPane.selectionModel.selectedIndex] as FileTabView).content.jumpToLine(line)
    }

    fun openFileOrFocusTab(file: File, line: Int = 0) {
        var newTab = tabPane.tabs.find { element ->
            return@find when (element) {
                is FileTabView -> {
                    element.file == file
                }
                else -> false
            }
        }

        if (newTab == null) {
            val lines = file.readLines()
            val tb = FileTabView(file)
            with(tb.content)
            {
                logger.info(smaliClass.makeInfoString())
                editorWindowView.replaceText(lines.joinToString("\n"))
                methodsNamesListView.items.addAll(smaliClass.methods)
                fieldsNamesListView.items.addAll(smaliClass.fields)
                interfacesNamesListView.items.addAll(smaliClass.interfacesNames)
            }
            tabPane.tabs.add(tb)
            newTab = tb
        }
        if (newTab is FileTabView) {
            newTab.content.jumpToLine(line)
        }
        tabPane.selectionModel.select(newTab)
    }

    fun closeProject() {
        Workspace.loadedClasses.clear()
        Workspace.workingDir = null
        tabPane.tabs.clear()
        filesTreeView.root?.children?.clear()
        packageTreeView.root?.children?.clear()
        filesTreeView.root = null
        packageTreeView.root = null
        packageTreeView.refresh()
        compileMenu.isDisable = true
        closeMenu.isDisable = true
    }

    private fun loadProject(projectDir: File) {

        title = AppConfig.CONST_APP_TITLE + Workspace.workingDir?.canonicalPath

        val rootSmali = SmaliClass()
        rootSmali.isPackage = true
        rootSmali.name = projectDir.name

        packageTreeView.root = TreeItem(rootSmali)
        filesTreeView.root = TreeItem<File>(projectDir)

        packageTreeView.cellFormat {
            text = it.name
            if (it.sourceName != "")
                text += " (${it.sourceName})"
            val image = when {
                it.isPackage -> ImageView(packageIcon)
                else -> ImageView(smaliIcon)
            }
            graphic = image
        }
        filesTreeView.cellFormat {
            val image = when {
                it.isDirectory -> ImageView(folderImage)
                it.extension == "smali" -> ImageView(smaliIcon)
                else -> ImageView(unknownIcon)
            }
            graphic = image
            text = it.name
        }
        filesTreeView.selectionModel.selectionMode = SelectionMode.SINGLE
        filesTreeView.setOnMouseClicked {
            if (it.clickCount == 2) {
                try {
                    val selectedFile = filesTreeView.selectionModel.selectedItem?.value
                    if (selectedFile == null || selectedFile.isDirectory)
                        return@setOnMouseClicked
                    openFileOrFocusTab(selectedFile)
                } catch (e: Exception) {
                    logger.error(e)
                }
            }
        }
        packageTreeView.setOnMouseClicked {
            if (it.clickCount == 2) {
                val smaliClass = packageTreeView.selectionModel.selectedItem?.value
                val selectedFile = smaliClass?.file ?: return@setOnMouseClicked
                openFileOrFocusTab(selectedFile)
            }
        }
        GlobalScope.launch {
            logger.info("Reading file treeview")
            fillTreeView(filesTreeView.root, projectDir)
            logger.info("Reading package treeview")
            fillPackageView(packageTreeView.root)
            logger.info("Project loaded!")
            Platform.runLater {
                compileMenu.isDisable = false
                closeMenu.isDisable = false //TODO move to function
            }
        }
    }

    fun openMenuHandler() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Open folder with apktool project..."
        Workspace.workingDir = directoryChooser.showDialog(primaryStage)
        Workspace.workingDir?.let {
            loadProject(it)
        }
    }

    fun decompileMenuHandler() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select apk to disassemble..."
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Android application", "*.apk"))
        val apkFile = fileChooser.showOpenDialog(primaryStage) ?: return
        closeProject()
        val projectFolder = File(AppConfig.PROJECT_ROOT + File.separator + apkFile.nameWithoutExtension)
        GlobalScope.launch {
            ApkToolWrapper.decompile(apkFile.absolutePath, projectFolder.canonicalPath)
            Platform.runLater {
                Workspace.workingDir = projectFolder
                loadProject(projectFolder)
            }
        }
    }

    fun compileMenuHandler() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select apk to disassemble..."
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Android application", "*.apk"))
        val apkFile = fileChooser.showSaveDialog(primaryStage) ?: return
        GlobalScope.launch {
            Workspace.workingDir?.canonicalPath?.let { ApkToolWrapper.compile(it, apkFile.canonicalPath) }
        }
    }


    fun findInProject() {
        logger.info("findInProject")
        val textDialog = TextInputDialog()
        textDialog.title = "Enter text to search"
        val dialogResult = textDialog.showAndWait()
        dialogResult?.ifPresent()
        { findTextAndShowDialog(it) }
    }

    fun onExitMenuHandler() {
        exitProcess(0)
    }
}
