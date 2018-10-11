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
        networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.LOADING, null))
        initialLoading.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.LOADING, null))

//        serviceApi.callRepos(_firstPage, _pageSize, queryText)
//                .enqueue(object : Callback<RepoResponse> {
//                    override fun onFailure(call: Call<RepoResponse>?, t: Throwable?) {
//                        networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.FAILED, t!!.message))
//
//                    }
//
//                    override fun onResponse(call: Call<RepoResponse>?, response: Response<RepoResponse>?) {
//                        if (response!!.isSuccessful) {
//                            callback.onResult(response.body()!!.items, null, _firstPage + 1)
//                            networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.LOADED, null))
//                        } else {
//                            networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.FAILED, response.message()))
//                        }
//                    }
//                })
        serviceApi.callRepos(_firstPage, _pageSize, queryText)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableObserver<RepoResponse>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: RepoResponse) {
                        t.items.let {
                            callback.onResult(it, null, _firstPage + 1)
                            networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.LOADED, null))
                        }
                    }

                    override fun onError(e: Throwable) {
                        networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.FAILED, e.message!!))
                    }
                })

    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, RepoResponse.Item>) {
        networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.LOADING, null))


//        serviceApi.callRepos(params.key.toLong(), _pageSize, queryText)
//                .enqueue(object : Callback<RepoResponse> {
//                    override fun onFailure(call: Call<RepoResponse>?, t: Throwable?) {
//                        networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.FAILED, t!!.message))
//
//                    }
//
//                    override fun onResponse(call: Call<RepoResponse>?, response: Response<RepoResponse>?) {
//                        if (response!!.isSuccessful) {
//                            val nextKey = (if (params.key === response.body()!!.count) null else params.key + 1)
//                            callback.onResult(response.body()!!.items, nextKey)
//                            networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.LOADED, null))
//                        } else {
//                            networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.FAILED, response.message()))
//                        }
//                    }
//                })

        serviceApi.callRepos(params.key.toLong(), _pageSize, queryText)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableObserver<RepoResponse>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: RepoResponse) {
                        t.items.let {
                            networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.LOADED, null))
                            val nextKey = (if (params.key == t.count) null else params.key + 1)
                            callback.onResult(it, nextKey)
                        }

                    }

                    override fun onError(e: Throwable) {
                        networkState.postValue(AppNetworkState.TooliStringState(AppNetworkState.State.FAILED, e.message!!))
                    }

                })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, RepoResponse.Item>) {
    }
}