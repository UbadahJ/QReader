package com.ubadahj.qidianundergroud

import com.ubadahj.qidianundergroud.api.Api
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Test

class ApiTests {

    @Test
    fun api_isWorking() {
        runBlocking {
            val books = Api.getBooks()
            assertFalse(books.isEmpty())
        }
    }

    @Test
    fun api_isProxyWorking() {
        runBlocking {
            val books = Api.getBooks(proxy = true)
            assertFalse(books.isEmpty())
        }
    }


}