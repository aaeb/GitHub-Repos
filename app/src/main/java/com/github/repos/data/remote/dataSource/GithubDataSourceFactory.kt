package com.github.repos.data.remote.dataSource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.github.repos.data.models.RepoResponse
import com.github.repos.data.remote.ServiceAPIs
import com.github.repos.rx.SchedulerProvider

class GithubDataSourceFactory(private val serviceAPIs: ServiceAPIs, private val schedulerProvider: SchedulerProvider, private var queryText: String) : DataSource.Factory<Long, RepoResponse.Item>() {

    var liveDataSource = MutableLiveData<GithubDataSource>()
    lateinit var githubDataSource: GithubDataSource


    /**
     * Create DataSource Object
     * */
    override fun create(): DataSource<Long, RepoResponse.Item> {
        githubDataSource = GithubDataSource(serviceAPIs, schedulerProvider, queryText)
        this.liveDataSource.postValue(githubDataSource)
        return this.githubDataSource
    }
}