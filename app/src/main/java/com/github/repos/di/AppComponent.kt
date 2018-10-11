package com.github.repos.di.Providers

import android.app.Application
import com.github.repos.di.ActivityBuilder
import com.github.repos.di.module.AppModule
import com.github.repos.di.module.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        ActivityBuilder::class))
interface AppComponent {

    fun inject(app: GithubApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

}
