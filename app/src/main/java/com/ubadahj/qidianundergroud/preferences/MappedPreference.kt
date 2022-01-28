package com.ubadahj.qidianundergroud.preferences

import com.fredporciuncula.flow.preferences.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.map

fun <T, R> Preference<T>.map(
    mapper: (T) -> R,
    reverse: (R) -> T
): Preference<R> = MappedPreference(this, mapper, reverse)

fun <T, R> Preference<T>.mapToEntries(entries: Map<T, R>): Preference<R> = map({
    entries[it]
        ?: entries[defaultValue]
        ?: throw IllegalArgumentException("No such value '$it' in entries")
}) {
    entries.entries.associate { (k, v) -> v to k }[it]
        ?: throw IllegalArgumentException("No such key '$it' in entries")
}

internal class MappedPreference<T, R>(
    private val preference: Preference<T>,
    private val mapper: (T) -> R,
    private val reverse: (R) -> T
) : Preference<R> {
    override val defaultValue: R
        get() = mapper(preference.defaultValue)
    override val key: String
        get() = preference.key

    override fun asCollector(): FlowCollector<R> = object : FlowCollector<R> {
        override suspend fun emit(value: R) {
            preference.asCollector().emit(reverse(value))
        }
    }

    override fun asFlow(): Flow<R> = preference.asFlow().map { mapper(it) }

    override fun asSyncCollector(throwOnFailure: Boolean): FlowCollector<R> =
        object : FlowCollector<R> {
            override suspend fun emit(value: R) {
                preference.asSyncCollector(throwOnFailure).emit(reverse(value))
            }
        }

    override fun delete() = preference.delete()

    override suspend fun deleteAndCommit(): Boolean = preference.deleteAndCommit()

    override fun get(): R = mapper(preference.get())

    override fun isNotSet(): Boolean = preference.isNotSet()

    override fun isSet(): Boolean = preference.isSet()

    override fun set(value: R) = preference.set(reverse(value))

    override suspend fun setAndCommit(value: R): Boolean = preference.setAndCommit(reverse(value))
}