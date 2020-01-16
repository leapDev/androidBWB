package com.learning.leap.bwb.baseActivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.BabbleApplication
import com.learning.leap.bwb.R
import com.learning.leap.bwb.download.DownloadActivity
import com.learning.leap.bwb.settings.userInfo.UserInfoActivity
import com.learning.leap.bwb.utility.Constant

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        goToCorrectScreen((application as BabbleApplication).appComponent.sharedPreferences)
    }

    private fun goToCorrectScreen(sharedPreferences:SharedPreferences){
        val didDownload = sharedPreferences.getBoolean(Constant.DID_DOWNLOAD,false)
        val needUpdate = sharedPreferences.getBoolean(Constant.UPDATE, false)
        if (didDownload && !needUpdate) {
            homeIntent()
        } else if (needUpdate) {
            downloadIntent()
        } else {
           userInfoIntent()
        }
    }

    private fun userInfoIntent() {
        val userInfoIntent = Intent(this, UserInfoActivity::class.java)
        userInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        userInfoIntent.putExtra(Constant.NEW_USER, true)
        startActivity(userInfoIntent)
    }

    private fun downloadIntent() {
        val downloadIntent = Intent(this, DownloadActivity::class.java)
        downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        downloadIntent.putExtra(Constant.UPDATE, true)
        startActivity(downloadIntent)
    }

    private fun homeIntent() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(homeIntent)
    }
}