package com.github.repos.di.module

import com.github.repos.BuildConfig
import com.github.repos.data.remote.ServiceAPIs
import com.github.repos.data.remote.ServiceGenerator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideServiceAPIs(): ServiceAPIs =
                ServiceGenerator().createService(BuildConfig.Base_Url, ServiceAPIs::class.java)

}