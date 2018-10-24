package com.github.repos.data.remote.dataSource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.github.repos.data.models.RepoResponse
import com.github.repos.data.remote.ServiceAPIs
import com.github.repos.rx.SchedulerProvider
import com.github.repos.utils.AppNetworkState
import io.reactivex.observers.DisposableObserver


class GithubDataSource(private val serviceApi: ServiceAPIs, private val schedulerProvider: SchedulerProvider, private var queryText: String) : PageKeyedDataSource<Long, RepoResponse.Item>() {

    val networkState = MutableLiveData<AppNetworkState>()
    val initialLoading = MutableLiveData<AppNetworkState>()


    //the size of a page that we want
    private val pageSize = 10

    //we will start from the first page which is 1
    private val firstPage = 1L


    /**
     * is called for the first page
     * */
    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, RepoResponse.Item>) {
        networkState.postValue(AppNetworkState(AppNetworkState.State.LOADING, null))
        initialLoading.postValue(AppNetworkState(AppNetworkState.State.LOADING, null))

        serviceApi.callRepos(firstPage, pageSize, queryText)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableObserver<RepoResponse>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: RepoResponse) {
                        t.items.let {
                            callback.onResult(it, null, firstPage + 1)
                            networkState.value = (AppNetworkState(AppNetworkState.State.LOADED, null))
                            initialLoading.value = (AppNetworkState(AppNetworkState.State.LOADED, null))
                        }
                    }

                    override fun onError(e: Throwable) {
                        networkState.value=(AppNetworkState(AppNetworkState.State.FAILED, e.message!!))
                        initialLoading.value=(AppNetworkState(AppNetworkState.State.FAILED, null))

                    }
                })

    }

    /**
     * is called after the first page calling
     * */
    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, RepoResponse.Item>) {
        networkState.postValue(AppNetworkState(AppNetworkState.State.LOADING, null))


        serviceApi.callRepos(params.key.toLong(), pageSize, queryText)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableObserver<RepoResponse>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: RepoResponse) {
                        t.items.let {
                            networkState.value=(AppNetworkState(AppNetworkState.State.LOADED, null))
                            val nextKey = (if (params.key == t.count) null else params.key + 1)
                            callback.onResult(it, nextKey)
                        }

                    }

                    override fun onError(e: Throwable) {
                        networkState.value=(AppNetworkState(AppNetworkState.State.FAILED, e.message!!))
                    }

                })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, RepoResponse.Item>) {
    }
}