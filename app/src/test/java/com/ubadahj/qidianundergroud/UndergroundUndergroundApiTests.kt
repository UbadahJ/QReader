package com.ubadahj.qidianundergroud

import com.ubadahj.qidianundergroud.api.retrofit.UndergroundApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Test

class UndergroundUndergroundApiTests {

    @Test
    fun api_isWorking() {
        runBlocking {
            val books = UndergroundApi.getBooks()
            assertFalse(books.isEmpty())
        }
    }

    @Test
    fun api_isProxyWorking() {
        runBlocking {
            val books = UndergroundApi.getBooks(proxy = true)
            assertFalse(books.isEmpty())
        }
    }

}
