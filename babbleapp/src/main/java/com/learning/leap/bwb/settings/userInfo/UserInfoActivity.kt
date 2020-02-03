package com.learning.leap.bwb.settings.userInfo

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.learning.leap.bwb.Player
import com.learning.leap.bwb.R
import com.learning.leap.bwb.download.DownloadActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.model.BabbleUser
import com.learning.leap.bwb.settings.WhyActivity
import com.learning.leap.bwb.userInfo.UserInfoPresenter
import com.learning.leap.bwb.userInfo.UserInfoViewInterface
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.DynamoDBSingleton
import com.learning.leap.bwb.utility.NetworkChecker
import com.learning.leap.bwb.utility.Utility
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_user_info.*
import java.util.*

class UserInfoActivity : AppCompatActivity(), UserInfoViewInterface {
    var newUser: Boolean = false
    var mDialog: ProgressDialog? = null
    var gender: String = "Now Now"

    lateinit var userInfoPresenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        onClickListners()
        newUser = intent.getBooleanExtra(Constant.NEW_USER, false)
        userInfoPresenter = UserInfoPresenter(newUser,this, LocalLoadSaveHelper(this),
                NetworkChecker(this),Realm.getDefaultInstance(),
                DynamoDBSingleton.getDynamoDB(this))
        if (!newUser) {
            userInfoPresenter.loadPlayerFromSharedPref()
        }
    }

    private fun onClickListners() {
        pleaseTapHereTextView.setOnClickListener { view: View? -> whyActivityIntent() }
        userProfileSaveButton.setOnClickListener { view: View? -> saveButtonClicked() }
        userMaleButton.setOnClickListener {
            maleButtonSelected()
            gender = "Male"
        }

        userFemaleButton.setOnClickListener {
            femaleButonSelected()
            gender = "Female"
        }

        userNotNowButton.setOnClickListener {
            notNowSelected()
            gender = "Not Now"
        }
    }

    private fun notNowSelected() {
        userMaleButton.setBackgroundColor(Color.LTGRAY)
        userFemaleButton.setBackgroundColor(Color.LTGRAY)
        userNotNowButton.setBackgroundColor(ContextCompat.getColor(this, R.color.darkestBlue))
    }

    private fun femaleButonSelected() {
        userMaleButton.setBackgroundColor(Color.LTGRAY)
        userFemaleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.darkestBlue))
        userNotNowButton.setBackgroundColor(Color.LTGRAY)
    }

    private fun maleButtonSelected() {
        userMaleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.darkestBlue))
        userFemaleButton.setBackgroundColor(Color.LTGRAY)
        userNotNowButton.setBackgroundColor(Color.LTGRAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        userInfoPresenter.onDestroy()
    }

    private fun whyActivityIntent() {
        val whyIntent = Intent(this, WhyActivity::class.java)
        startActivity(whyIntent)
    }

    private fun saveButtonClicked() {
        userInfoPresenter.createBabblePlayer(createBabblePlayer())
        userInfoPresenter.checkUserInput()
    }

    private fun createBabblePlayer(): BabbleUser {
            return BabbleUser(UUID.randomUUID().toString(), userProfileFragmentBirtdayEditText.text.toString().trim { it <= ' ' },
                    userProfileFragmentFirstNameEditText.text.toString().trim { it <= ' ' },gender)

    }

    override fun displayErrorDialog(dialogTitle: Int, dialogMessage: Int) {
        runOnUiThread { Utility.displayAlertMessage(dialogTitle, dialogMessage, this) }
    }

    override fun displayUserInfo(babbleUser: BabbleUser) {
        userProfileFragmentBirtdayEditText.setText(babbleUser.babyBirthday)
        userProfileFragmentFirstNameEditText.setText(babbleUser.babyName)
        when (babbleUser.babyGender) {
            "Male" -> maleButtonSelected()
            "Female" -> femaleButonSelected()
            else -> notNowSelected()
        }
    }

    override fun dismissActivity() {
        Utility.addCustomEvent(Constant.CHANGE_PROFILE_SETTINGS, Utility.getUserID(this), null)
        finish()
    }

    override fun downloadIntent() {
        val downloadIntent = Intent(this@UserInfoActivity, DownloadActivity::class.java)
        startActivity(downloadIntent)
    }


    override fun getUserGender(): String {
        return gender
    }

    override fun displaySaveDialog() {
        runOnUiThread { createDialog() }
    }

    private fun createDialog() {
        mDialog = ProgressDialog(this@UserInfoActivity)
        mDialog!!.setMessage("Saving Player Info")
        mDialog!!.setTitle("Beginning to Babble")
        mDialog!!.show()
    }

    override fun dismissSaveDialog() {
        if (mDialog != null) {
            runOnUiThread { mDialog!!.dismiss() }
        }
    }
}