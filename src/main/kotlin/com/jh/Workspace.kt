package com.jh

import com.jh.smaliStructs.SmaliClass
import java.io.File

object Workspace {
    var workingDir: File? = null
    val loadedClasses = ArrayList<SmaliClass>()
    private fun walkFiles(root: File) {
        val filesInDir = root.listFiles { f ->
            return@listFiles (f.isDirectory || f.extension == "smali")
        }
        for (file in filesInDir) {

            if (file.isDirectory)
                walkFiles(file)
            else {
                workingDir?.let {
                    loadedClasses.add(SmaliClass.parse(file.readLines(), file))
                }
            }
        }
    }

    fun loadClasses() {
        loadedClasses.clear()
        workingDir?.let {
            val smaliFolders = it.listFiles { f ->
                return@listFiles f.name.matches(Regex("^smali_classes\\d+$|^smali$"))
            }
            for (smaliFolder in smaliFolders) {
                walkFiles(smaliFolder)
            }
        }
    }
}