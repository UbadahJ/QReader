package com.ubadahj.qidianundergroud.services.updater.models

data class Version(
    val version: String,
    val build: Int,
    val branch: String,
    val increment: Int,
) : Comparable<Version> {

    companion object {
        fun create(versionStr: String): Version {
            val (code, branch) = versionStr.split("-", limit = 2)
            val increments = code.split("+")
            val increment = if (increments.size == 2) increments.last().toInt() else 0
            val (version, build) = increments.first().split("b", limit = 2)

            return Version(version, build.toInt(), branch, increment)
        }
    }

    override fun compareTo(other: Version): Int = build.compareTo(other.build)

}