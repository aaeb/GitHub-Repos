package com.github.repos.data.remote.dataSource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.github.repos.data.models.RepoResponse
import com.github.repos.data.remote.ServiceAPIs
import com.github.repos.rx.SchedulerProvider
import com.github.repos.utils.AppNetworkState
import com.github.repos.utils.SingleLiveEvent
import io.reactivex.observers.DisposableObserver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result.response


class GithubDataSource(private val serviceApi: ServiceAPIs, private val schedulerProvider: SchedulerProvider, private var queryText: String) : PageKeyedDataSource<Long, RepoResponse.Item>() {

    private var networkState = MutableLiveData<AppNetworkState>()
    private var initialLoading = MutableLiveData<AppNetworkState>()


    //the size of a page that we want
    private val _pageSize = 10

    //we will start from the first page which is 1
    private val _firstPage = 1L

    val getNetworkState
        get() = networkState

    val getInitialLoading
        get() = initialLoading


    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, RepoResponse.Item>) {
        networkState.postValue(AppNetworkState(AppNetworkState.State.LOADING, null))
        initialLoading.postValue(AppNetworkState(AppNetworkState.State.LOADING, null))

        serviceApi.callRepos(_firstPage, _pageSize, queryText)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableObserver<RepoResponse>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: RepoResponse) {
                        t.items.let {
                            callback.onResult(it, null, _firstPage + 1)
                            networkState.postValue(AppNetworkState(AppNetworkState.State.LOADED, null))
                            initialLoading.postValue(AppNetworkState(AppNetworkState.State.LOADED, null))

                        }
                    }

                    override fun onError(e: Throwable) {
                        networkState.postValue(AppNetworkState(AppNetworkState.State.FAILED, e.message!!))
                        initialLoading.postValue(AppNetworkState(AppNetworkState.State.FAILED, null))

                    }
                })

    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, RepoResponse.Item>) {
        networkState.postValue(AppNetworkState(AppNetworkState.State.LOADING, null))


        serviceApi.callRepos(params.key.toLong(), _pageSize, queryText)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableObserver<RepoResponse>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: RepoResponse) {
                        t.items.let {
                            networkState.postValue(AppNetworkState(AppNetworkState.State.LOADED, null))
                            val nextKey = (if (params.key == t.count) null else params.key + 1)
                            callback.onResult(it, nextKey)
                        }

                    }

                    override fun onError(e: Throwable) {
                        networkState.postValue(AppNetworkState(AppNetworkState.State.FAILED, e.message!!))
                    }

                })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, RepoResponse.Item>) {
    }
}