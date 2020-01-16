package com.learning.leap.bwb.di

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.learning.leap.bwb.BabbleApplication
import com.learning.leap.bwb.baseActivity.SplashActivity
import dagger.BindsInstance
import dagger.Component
import io.realm.Realm
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    val connectivityManager:ConnectivityManager
    val sharedPreferences:SharedPreferences
    val realm:Realm
    val credentialsProvider: CognitoCachingCredentialsProvider

    fun inject(splashActivity: SplashActivity)

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context):AppComponent
    }

}

val Activity.injector get() = (application as BabbleApplication).appComponent