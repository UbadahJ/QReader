package com.ubadahj.qidianundergroud.services.updater.api

import com.ubadahj.qidianundergroud.api.RetrofitProvider
import com.ubadahj.qidianundergroud.services.updater.models.Release
import retrofit2.http.GET
import javax.inject.Inject

class UpdateApi @Inject constructor(
    retrofitProvider: RetrofitProvider
) {

    private val api = retrofitProvider
        .get("https://api.github.com")
        .create(GithubApi::class.java)

    suspend fun checkRelease(): Release {
        return api.checkRelease()
    }

    private interface GithubApi {
        @GET("/repos/UbadahJ/QReader/releases/latest")
        suspend fun checkRelease(): Release
    }
}