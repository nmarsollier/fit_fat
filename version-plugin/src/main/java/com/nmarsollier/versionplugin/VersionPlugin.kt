package com.nmarsollier.versionplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories

const val DIRECTORY = "buildSrc/src/main/kotlin"

class VersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        Path.of(DIRECTORY).createDirectories()

        Thread.currentThread().contextClassLoader.getResourceAsStream("Libraries.kt").use { input ->
            val destFile = File("$DIRECTORY/Libraries.kt")
            if (destFile.exists()) {
                destFile.delete()
            }
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        project.logger.warn("VersionProject plugin applied")
    }
}
