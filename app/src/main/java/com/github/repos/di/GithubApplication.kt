package com.github.repos.di.Providers

import android.app.Activity
import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication

import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

open class GithubApplication : MultiDexApplication(), HasActivityInjector {
    //    @Inject
//    lateinit var mCalligraphyConfig: CalligraphyConfig
    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }


    override fun onCreate() {
        super.onCreate()
//        AppLogger.init()
//        initFabric(this)

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

//        CalligraphyConfig.initDefault(mCalligraphyConfig)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
