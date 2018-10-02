package com.jh.util

import com.jh.SmaliAppLauncher
import com.jh.Workspace
import com.jh.smaliStructs.SmaliClass
import com.jh.views.ModernView
import com.jh.views.search.SearchResultsView
import com.jh.views.search.SearchTableDataClass
import javafx.application.Platform
import javafx.scene.image.Image
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.apache.logging.log4j.LogManager
import tornadofx.*
import java.io.File
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val logger = LogManager.getLogger("Utils.kt")

suspend fun String.executeInShell() {
    val proc = Runtime.getRuntime().exec("cmd /c $this")
    val inStreamScanner = Scanner(proc.inputStream)
    val errStreamScanner = Scanner(proc.errorStream)
    val iStreamJob = GlobalScope.launch {
        while (inStreamScanner.hasNext())
            logger.info(inStreamScanner.nextLine())
    }
    val eStreamJob = GlobalScope.launch {
        while (errStreamScanner.hasNext())
            logger.error(errStreamScanner.nextLine())
    }
    iStreamJob.join()
    eStreamJob.join()
    logger.info("Process finished with code ${proc.exitValue()}!")
}

fun findStringInText(text: String, string: String): ArrayList<Int> {
    val p = Pattern.compile("""(${Regex.escape(string)})""") ?: return ArrayList()
    val res = ArrayList<Int>()
    val matcher = p.matcher(text)
    while (matcher.find()) {
        matcher.group()?.let {
            val pos = matcher.start()
            val line = text.substring(0, pos).count { char -> return@count char == '\n' }
            res.add(line)
        }
    }
    return res
}

suspend fun findSmaliFilesWithTextInDir(dir: File, text: String): Map<out File, ArrayList<Int>> {
    val res = HashMap<File, ArrayList<Int>>()
    val files = dir.listFiles() ?: return res
    for (file in files) {
        when {
            file.isDirectory -> res.putAll(findSmaliFilesWithTextInDir(file, text))
            file.extension == "smali" -> {
                val sourceText = file.readText()
                val searchResult = findStringInText(sourceText, text)
                if (searchResult.isNotEmpty())
                    res[file] = searchResult
            }
        }
    }
    return res
}

suspend fun findSmaliClasssesWithTextInProject(text: String): Map<out SmaliClass, ArrayList<Int>> {
    val res = HashMap<SmaliClass, ArrayList<Int>>()
    for (clazz in Workspace.loadedClasses) {
        val sourceText = clazz.file?.readText() ?: continue
        val searchResult = findStringInText(sourceText, text)
        if (searchResult.isNotEmpty()) {
            res[clazz] = searchResult
        }
    }
    return res
}

fun findTextAndShowDialog(text: String) {
    val searchResultsView = SearchResultsView(Workspace.mainView)
    val job = GlobalScope.launch {
        if (Workspace.workingDir != null) {
            logger.info("About to find $text in smali files")
            val res = findSmaliClasssesWithTextInProject(text)
            logger.info("Search finished!")
            for (smaliClass in res.keys) {
                val positions = res[smaliClass]
                positions?.forEach {
                    searchResultsView.addLineToResultTable(SearchTableDataClass(smaliClass, it))
                    logger.info("class ${smaliClass.name} in package ${smaliClass.packageName} line $it")
                }
            }
        }
    }
    job.invokeOnCompletion {
        Platform.runLater {
            searchResultsView.openWindow()
        }
    }
}

fun loadPicture(path: String) = Image(SmaliAppLauncher::class.java.getResourceAsStream(path))
fun loadCss(path: String) = SmaliAppLauncher::class.java.getResource(path).toExternalForm()