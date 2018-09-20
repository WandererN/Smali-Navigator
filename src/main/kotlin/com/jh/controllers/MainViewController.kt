package com.jh.controllers

import com.jh.AppConfig
import com.jh.SmaliAppLauncher
import com.jh.Workspace
import com.jh.extToolLogger.TextBoxTextLogger
import com.jh.smaliStructs.SmaliClass
import com.jh.toolsWrappers.ApkToolWrapper
import com.jh.views.ExternalToolLoggerView
import com.jh.views.MainView
import com.jh.views.TabEditorView
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class MainViewController : Controller() {
    private val folderImage = Image(SmaliAppLauncher::class.java.getResourceAsStream("/icons/folder_icon.png"))
    private val smaliIcon = Image(SmaliAppLauncher::class.java.getResourceAsStream("/icons/smali_file_icon.png"))
    private val unknownIcon = Image(SmaliAppLauncher::class.java.getResourceAsStream("/icons/unknown_file_icon.png"))
    private val packageIcon = Image(SmaliAppLauncher::class.java.getResourceAsStream("/icons/package_icon.png"))
    //TODO methods rename
    //TODO add logging to selected method
    //TODO future :: add more file formats
    private fun openFileOrFocusTab(file: File, mainView: MainView) {
        var newTab = mainView.sourcesTabs.tabs.find { element ->
            return@find when (element) {
                is TabEditorView -> {
                    element.currentFile == file
                }
                else -> false
            }
        }

        if (newTab == null) {
            val lines = file.readLines()
            newTab = TabEditorView(file)
            newTab.editorWindowView.replaceText(lines.joinToString("\n"))
            newTab.methodsNamesListView.items.addAll(newTab.smaliClass.methods)
            mainView.sourcesTabs.tabs.add(newTab)
        }
        mainView.sourcesTabs.selectionModel.select(newTab)
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

    private fun fillPackageView(root: TreeItem<SmaliClass>) {
        for (clazz in Workspace.loadedClasses) {
            val treeItem = findOrCreatePackage(clazz.packageName, root)
            treeItem.children.add(TreeItem(clazz))
        }
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
                Workspace.loadedClasses.add(SmaliClass.parse(it.readLines(), it, Workspace.workingDir!!))
            }
        }
        root.children.addAll(filesList)
    }

    fun openMenuHandler(mainView: MainView) {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Open folder with apktool project..."
        Workspace.workingDir = directoryChooser.showDialog(primaryStage)
        if (Workspace.workingDir == null)
            return
        mainView.title = AppConfig.CONST_APP_TITLE + Workspace.workingDir?.canonicalPath
        //  Workspace.loadClasses()
        mainView.filesTreeView.root = TreeItem<File>(Workspace.workingDir)
        val rootSmali = SmaliClass()
        rootSmali.isPackage = true
        rootSmali.name = Workspace.workingDir!!.name
        mainView.packageTreeView.root = TreeItem(rootSmali)
        Thread {
            fillTreeView(mainView.filesTreeView.root, Workspace.workingDir!!)
            fillPackageView(mainView.packageTreeView.root)
        }.start()

        mainView.packageTreeView.cellFormat {

            text = it.name
            if (it.sourceName != "")
                text += " (${it.sourceName})"
            var image = when {
                it.isPackage -> ImageView(packageIcon)
                else -> ImageView(smaliIcon)
            }
            graphic = image
        }
        mainView.filesTreeView.cellFormat {
            val image = when {
                it.isDirectory -> ImageView(folderImage)
                it.extension == "smali" -> ImageView(smaliIcon)
                else -> ImageView(unknownIcon)
            }
            graphic = image
            text = it.name
        }
        mainView.filesTreeView.selectionModel.selectionMode = SelectionMode.SINGLE
        mainView.filesTreeView.setOnMouseClicked {
            if (it.clickCount == 2) {
                val selectedFile = mainView.filesTreeView.selectionModel.selectedItem.value
                if (selectedFile.isDirectory)
                    return@setOnMouseClicked
                openFileOrFocusTab(selectedFile, mainView)
            }
        }
        mainView.packageTreeView.setOnMouseClicked {
            if (it.clickCount == 2) {
                val selectedFile = mainView.packageTreeView.selectionModel.selectedItem.value.file
                        ?: return@setOnMouseClicked
                openFileOrFocusTab(selectedFile, mainView)
            }
        }
    }

    fun decompileMenuHandler() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select apk to disassemble..."
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Android application", "*.apk"))
        val apkFile = fileChooser.showOpenDialog(primaryStage) ?: return
        val extView = ExternalToolLoggerView()
        AppConfig.extUtilsLogger = TextBoxTextLogger(textBox = extView.myTextArea)
        extView.openModal()
        ApkToolWrapper.decompile(apkFile.absolutePath, AppConfig.PROJECT_ROOT + File.pathSeparator + "out")
        // extView.close()
    }

    fun compileMenuHandler() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select apk to disassemble..."
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Android application", "*.apk"))
        val apkFile = fileChooser.showSaveDialog(primaryStage) ?: return
        val extView = ExternalToolLoggerView()
        AppConfig.extUtilsLogger = TextBoxTextLogger(textBox = extView.myTextArea)
        extView.openModal()
        Workspace.workingDir?.canonicalPath?.let { ApkToolWrapper.compile(it, apkFile.canonicalPath) }
    }
}