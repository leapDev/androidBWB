package com.learning.leap.bwb.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.learning.leap.bwb.utility.Constant
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.realm.Realm
import javax.inject.Singleton

@Module
class AppModule {
    private val sharedPreferencesFile = "Global"


    @Singleton
    @Provides
    fun provideCognitoCachingCredentialsProvider(context: Context):CognitoCachingCredentialsProvider{
        return CognitoCachingCredentialsProvider(
                context.applicationContext,
                Constant.CognitoIdentityPoolId,  /* Identity Pool ID */
                Regions.US_EAST_1 /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )
    }

    @Reusable
    @Provides
    fun provideSharedPref(context: Context):SharedPreferences{
        return context.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    fun provideRealm(context: Context):Realm{
        return Realm.getDefaultInstance()
    }

    @Provides
    @Reusable
    fun provideConnectivityManager(context: Context):ConnectivityManager{
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}