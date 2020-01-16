package com.learning.leap.bwb.settings.userInfo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.learning.leap.bwb.BuildConfig
import com.learning.leap.bwb.Player
import com.learning.leap.bwb.R
import com.learning.leap.bwb.download.DownloadActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.models.BabblePlayer
import com.learning.leap.bwb.research.ResearchPlayers
import com.learning.leap.bwb.settings.WhyActivity
import com.learning.leap.bwb.userInfo.UserInfoPresenter
import com.learning.leap.bwb.userInfo.UserInfoViewInterface
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.NetworkChecker
import com.learning.leap.bwb.utility.Utility
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_user_profile.*
import javax.inject.Inject

class UserInfoActivity : AppCompatActivity(), UserInfoViewInterface {
    var newUser: Boolean? = null
    var mDialog: ProgressDialog? = null
    var gender: String? = null


    @Inject
    lateinit var userInfoPresenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user_profile)
        setupOnClickListners()

        newUser = intent.getBooleanExtra(Constant.NEW_USER, false)

        if (!newUser!!) {
            userInfoPresenter.loadPlayerFromSharedPref()
        }
    }


    private fun setupOnClickListners() {
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
        userInfoPresenter!!.createBabblePlayer(createBabblePlayer())
        userInfoPresenter!!.checkUserInput()
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
            babblePlayer.babbleID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
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