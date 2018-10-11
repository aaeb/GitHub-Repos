package com.github.repos.data.remote.dataSource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.github.repos.data.models.RepoResponse
import com.github.repos.data.remote.ServiceAPIs
import com.github.repos.rx.SchedulerProvider

class GithubDataSourceFactory(private val serviceAPIs: ServiceAPIs, private val schedulerProvider: SchedulerProvider, private var queryText: String) : DataSource.Factory<Long, RepoResponse.Item>() {

    private var _liveDataSource = MutableLiveData<GithubDataSource>()
    private lateinit var _githubDataSource: GithubDataSource

    val liveDataSource
        get() = _liveDataSource


    override fun create(): DataSource<Long, RepoResponse.Item> {
        _githubDataSource = GithubDataSource(serviceAPIs, schedulerProvider, queryText)
        _liveDataSource.postValue(_githubDataSource)
        return this._githubDataSource
    }
}