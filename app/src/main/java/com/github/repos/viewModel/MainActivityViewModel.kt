package com.github.repos.viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.github.repos.data.models.RepoResponse
import com.github.repos.data.remote.ServiceAPIs
import com.github.repos.data.remote.dataSource.GithubDataSource
import com.github.repos.data.remote.dataSource.GithubDataSourceFactory
import com.github.repos.utils.AppNetworkState
import com.github.repos.rx.*

class MainActivityViewModel(var serviceApi: ServiceAPIs, var schedulerProvider: SchedulerProvider) {

    private lateinit var _liveGithubResponse: LiveData<PagedList<RepoResponse.Item>>
    private lateinit var _liveGithubDataSource: MutableLiveData<GithubDataSource>
    private lateinit var _networkState: LiveData<AppNetworkState>
    private lateinit var _initialState: LiveData<AppNetworkState>
    private lateinit var liveGithubDataSourceFactory: GithubDataSourceFactory

    val liveGithubResponse: LiveData<PagedList<RepoResponse.Item>>
        get() = _liveGithubResponse

    val liveGithubDataSource
        get() = _liveGithubDataSource

    val networkState
        get() = _networkState

    val initialState
        get() = _initialState

    fun callGithubRepos(query: String) {

        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false)
                .setPageSize(10).build()

        liveGithubDataSourceFactory = GithubDataSourceFactory(serviceApi, schedulerProvider, query)
        _liveGithubDataSource = liveGithubDataSourceFactory.liveDataSource

        _networkState = Transformations.switchMap(liveGithubDataSourceFactory.liveDataSource
        ) { dataSource -> dataSource.getNetworkState }

        _initialState = Transformations.switchMap(liveGithubDataSourceFactory.liveDataSource
        ) { dataSource -> dataSource.getInitialLoading }


        val builder = LivePagedListBuilder<Long, RepoResponse.Item>(liveGithubDataSourceFactory, pagedListConfig)
        _liveGithubResponse = builder.build()

    }
}