package com.jh.util

import com.jh.SmaliAppLauncher
import javafx.scene.image.Image
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.apache.logging.log4j.LogManager
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

suspend fun findSmaliFilesWithTextInDir(dir: File, text: String): Map<out File, ArrayList<Int>> {
    val p = Pattern.compile("""($text)""") ?: return HashMap()
    val res = HashMap<File, ArrayList<Int>>()
    val files = dir.listFiles() ?: return res
    for (file in files) {
        when {
            file.isDirectory -> res.putAll(findSmaliFilesWithTextInDir(file, text))
            file.extension == "smali" -> {
                val sourceText = file.readText()
                val matcher = p.matcher(sourceText)
                while (matcher.find()) {
                    matcher.group()?.let {
                        val pos = matcher.start()
                        val line = sourceText.substring(0, pos).count { char -> return@count char == '\n' } + 1
                        if (res.containsKey(file)) {
                            res[file]?.add(line)
                        } else {
                            res[file] = arrayListOf(line)
                        }
                    }
                }
            }
        }
    }
    return res
}


fun loadPicture(path: String) = Image(SmaliAppLauncher::class.java.getResourceAsStream(path))
