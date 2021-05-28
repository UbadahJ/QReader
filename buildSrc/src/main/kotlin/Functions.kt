import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun Project.runCommand(command: String): String {
    val byteOut = ByteArrayOutputStream()
    exec {
        commandLine = command.split(" ")
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

fun Project.getAppVersion(): String {
    var version = "1.0b${getMasterCommitCount()}"
    if (getBranchName() != "master") {
        version += "+${runCommand("git rev-list --count HEAD ^master")}"
    }

    return "$version-${getBranchName()}"
}

fun Project.getMasterCommitCount(): String = runCommand("git rev-list --count master")

fun Project.getGitSha(): String = runCommand("git rev-parse --short HEAD")

fun Project.getBranchName(): String = runCommand("git rev-parse --abbrev-ref HEAD")

fun getBuildTime(): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date())