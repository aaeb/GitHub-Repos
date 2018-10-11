package com.github.repos.di.module

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.github.repos.data.adapers.ReposAdapter
import com.github.repos.data.remote.ServiceAPIs
import com.github.repos.rx.SchedulerProvider
import com.github.repos.utils.ViewModelProviderFactory
import com.github.repos.viewModel.MainActivityViewModel
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun provideHomeActivityViewModel(serviceApi: ServiceAPIs, schedulerProvider: SchedulerProvider): MainActivityViewModel {
        return MainActivityViewModel(serviceApi, schedulerProvider)
    }

    @Provides
    fun homeViewModelFactory(mViewModel: MainActivityViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(mViewModel)
    }

    @Provides
    fun provideLinearLayoutManager(context: Context): LinearLayoutManager {
        return LinearLayoutManager(context)
    }

    @Provides
    fun provideRepoAdapter(): ReposAdapter {
        return ReposAdapter()
    }
}