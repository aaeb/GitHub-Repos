package com.github.repos.data.remote

import com.github.repos.data.models.RepoResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*


interface ServiceAPIs {

    @GET("search/repositories?")
    fun callRepos(@Query("page") page: Long,
                  @Query("per_page") pageSize: Int,
                  @Query("q") serial: String): Observable<RepoResponse>
}
