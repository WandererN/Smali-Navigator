package com.jh.util

import com.jh.SmaliAppLauncher
import javafx.scene.image.Image
import org.apache.logging.log4j.LogManager
import java.util.*

fun String.executeInShell() {
    val logger = LogManager.getLogger(this.javaClass.name)
    val proc = Runtime.getRuntime().exec("cmd /c $this")
    var hasNext = true
    val inStreamScanner = Scanner(proc.inputStream)
    val errStreamScanner = Scanner(proc.errorStream)
    while (hasNext) {
        if (inStreamScanner.hasNext())
            logger.info(inStreamScanner.nextLine())

        if (errStreamScanner.hasNext())
            logger.error(errStreamScanner.nextLine())

        hasNext = inStreamScanner.hasNext() || errStreamScanner.hasNext()
    }
    logger.info("Process finished with code ${proc.exitValue()}!")
}

fun loadPicture(path: String) = Image(SmaliAppLauncher::class.java.getResourceAsStream(path))
