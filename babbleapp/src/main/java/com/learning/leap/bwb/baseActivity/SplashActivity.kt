package com.learning.leap.bwb.baseActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.R
import com.learning.leap.bwb.UpdatedToTwoActivity
import com.learning.leap.bwb.download.DownloadActivity
import com.learning.leap.bwb.settings.userInfo.UserInfoActivity
import com.learning.leap.bwb.utility.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    private val sharedPreferencesFile = "Global"
    private var disposable:Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        supportActionBar?.hide()
        disposable = Observable.timer(2,TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            goToCorrectScreen(provideSharedPref(this))
        }
    }

    fun provideSharedPref(context: Context):SharedPreferences{
        return context.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE);
    }


    private fun goToCorrectScreen(sharedPreferences:SharedPreferences){
        val didDownload = sharedPreferences.getBoolean(Constant.DID_DOWNLOAD,false)
        val needUpdate = sharedPreferences.getBoolean(Constant.UPDATE, false)
        val didUpdateToTwo = sharedPreferences.getBoolean(Constant.UPDATE_TO_TWO,false)

        if(didDownload && !didUpdateToTwo){
            updateIntent()
            return
        }
        if (didDownload && !needUpdate) {
            homeIntent()
        } else if (needUpdate) {
            downloadIntent()
        } else {
           userInfoIntent()
        }
    }

    private fun updateIntent(){
        val updateIntent = Intent(this, UpdatedToTwoActivity::class.java)
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(updateIntent)
    }

    private fun userInfoIntent() {
        val userInfoIntent = Intent(this, UserInfoActivity::class.java)
        userInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        userInfoIntent.putExtra(Constant.NEW_USER, true)
        startActivity(userInfoIntent)
    }

    private fun downloadIntent() {
        val downloadIntent = Intent(this, DownloadActivity::class.java)
        downloadIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        downloadIntent.putExtra(Constant.UPDATE, true)
        startActivity(downloadIntent)
    }

    private fun homeIntent() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(homeIntent)
    }
}