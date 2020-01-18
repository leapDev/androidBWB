package com.learning.leap.bwb.settings.userInfo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.Player
import com.learning.leap.bwb.R
import com.learning.leap.bwb.download.DownloadActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.models.BabblePlayer
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
    var newUser: Boolean? = null
    var mDialog: ProgressDialog? = null
    var gender: String? = null

    lateinit var userInfoPresenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        onClickListners()
        newUser = intent.getBooleanExtra(Constant.NEW_USER, false)
        userInfoPresenter = UserInfoPresenter(newUser,this, LocalLoadSaveHelper(this),
                NetworkChecker(this),Realm.getDefaultInstance(),
                DynamoDBSingleton.getDynamoDB(this))
        if (!newUser!!) {
            userInfoPresenter.loadPlayerFromSharedPref()
        }
    }

    private fun onClickListners() {
        pleaseTapHereTextView.setOnClickListener { view: View? -> whyActivityIntent() }
        userProfileFragmentSaveButton.setOnClickListener { view: View? -> saveButtonClicked() }
    }

    override fun onDestroy() {
        super.onDestroy()
        userInfoPresenter.onDestory()
    }

    private fun whyActivityIntent() {
        val whyIntent = Intent(this, WhyActivity::class.java)
        startActivity(whyIntent)
    }

    private fun saveButtonClicked() {
        userInfoPresenter.createBabblePlayer(createBabblePlayer())
        userInfoPresenter.checkUserInput()
    }

    private fun setGender() {
        gender = if (maleRadioButton!!.isChecked) {
            "Male"
        } else if (femaleRadioButton!!.isChecked) {
            "Female"
        } else {
            "Not Now"
        }
    }

    private fun createBabblePlayer(): Player {
            val babblePlayer = BabblePlayer()
            babblePlayer.babyName = userProfileFragmentFirstNameEditText.text.toString().trim { it <= ' ' }
            babblePlayer.zipCode = setZipCode()
            babblePlayer.babyBirthday =userProfileFragmentBirtdayEditText.text.toString().trim { it <= ' ' }
            babblePlayer.babbleID = UUID.randomUUID().toString();
            setGender()
            babblePlayer.babyGender = gender
            return babblePlayer

    }

    private fun setZipCode(): Int {
        return try {
            userProfileFragmentZipCodeEditText.text.toString().trim { it <= ' ' }.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun displayErrorDialog(dialogTitle: Int, dialogMessage: Int) {
        runOnUiThread { Utility.displayAlertMessage(dialogTitle, dialogMessage, this) }
    }

    override fun dismissActivity() {
        Utility.addCustomEvent(Constant.CHANGE_PROFILE_SETTINGS, Utility.getUserID(this), null)
        finish()
    }

    override fun downloadIntent() {
        val downloadIntent = Intent(this@UserInfoActivity, DownloadActivity::class.java)
        startActivity(downloadIntent)
    }

    override fun displayUserInfo(babblePlayer: Player) {
       userProfileFragmentBirtdayEditText.setText(babblePlayer.babyBirthday)
        userProfileFragmentFirstNameEditText.setText(babblePlayer.babyName)
        userProfileFragmentZipCodeEditText.setText(babblePlayer.zipCode.toString())
        when (babblePlayer.babyGender) {
            "Male" -> maleRadioButton!!.isChecked = true
            "Female" -> femaleRadioButton!!.isChecked = true
            else -> notNowRadioButton!!.isChecked = true
        }
    }

    override fun getUserGender(): String {
        return gender!!
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