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

    lateinit var liveGithubResponse: LiveData<PagedList<RepoResponse.Item>>
    lateinit var liveGithubDataSource: MutableLiveData<GithubDataSource>
    lateinit var networkState: LiveData<AppNetworkState>
    lateinit var initialState: LiveData<AppNetworkState>
    lateinit var liveGithubDataSourceFactory: GithubDataSourceFactory


    fun callGithubRepos(query: String) {

        val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false)
                .setPageSize(10).build()

        liveGithubDataSourceFactory = GithubDataSourceFactory(serviceApi, schedulerProvider, query)
        this.liveGithubDataSource = liveGithubDataSourceFactory.liveDataSource

        // SwitchMap between networkState LiveData and DataSource networkState
        this.networkState = Transformations.switchMap(liveGithubDataSourceFactory.liveDataSource
        ) { dataSource -> dataSource.networkState }

        // SwitchMap between networkState LiveData and DataSource networkState
        this.initialState = Transformations.switchMap(liveGithubDataSourceFactory.liveDataSource
        ) { dataSource -> dataSource.initialLoading }


        val builder = LivePagedListBuilder<Long, RepoResponse.Item>(liveGithubDataSourceFactory, pagedListConfig)
        this.liveGithubResponse = builder.build()

    }
}