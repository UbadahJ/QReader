import org.gradle.api.Project
import java.io.File

fun Project.createSpotlessGitHook() {
    val gitHooksDirectory = File("${this.rootDir}/.git/hooks/")
    if (!gitHooksDirectory.exists())
        gitHooksDirectory.mkdirs()

    val file = File("${this.rootDir}/.git/hooks", "pre-commit")

    file.bufferedWriter().use {
        it.write(
            """#!/bin/bash
                |echo "Running spotless check"
                |./gradlew spotlessApply
                |git add .
                |""".trimMargin()
        )
    }

    runCommand("chmod +x ${this.rootDir}/.git/hooks/pre-commit")
    println("Added pre-commit hook for spotless")
}