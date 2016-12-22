package com.learning.leap.beginningtobabbleapp.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.learning.leap.beginningtobabbleapp.download.DownloadActivity;
import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.userInfo.UserInfoPresenter;
import com.learning.leap.beginningtobabbleapp.userInfo.UserInfoViewInterface;
import com.learning.leap.beginningtobabbleapp.utility.Utility;
import com.learning.leap.beginningtobabbleapp.models.BabblePlayer;
import com.learning.leap.beginningtobabbleapp.models.Notification;

import java.util.List;

import rx.Subscription;

public class UserInfoActivity extends AppCompatActivity implements UserInfoViewInterface {
    EditText birthDayEditText;
   EditText zipCodeEditText;
     EditText firstNameEditText;
    Button saveButton;
    TextView pleaseTapHereTextView;
    Boolean newUser;
    ProgressDialog mDialog;
    Subscription savePlayerSubscrtion;
    Subscription retriveNotificationsSubscrtion;
    List<Notification> awsNotifications;
    UserInfoPresenter userInfoPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);
        setUpViews();
        newUser = getIntent().getBooleanExtra("newUser",false);
        userInfoPresenter = new UserInfoPresenter(this,newUser,this);
        if (!newUser){
           userInfoPresenter.loadPlayerFromSharedPref();
        }
    }

    private void setUpViews(){
        birthDayEditText = (EditText) findViewById(R.id.userProfileFragmentBirtdayEditText);
        zipCodeEditText = (EditText) findViewById(R.id.userProfileFragmentZipCodeEditText);
        firstNameEditText = (EditText) findViewById(R.id.userProfileFragmentFirstNameEditText);
        saveButton = (Button)findViewById(R.id.userProfileFragmentSaveButton);
        pleaseTapHereTextView = (TextView)findViewById(R.id.pleaseTapHereTextView);
        setupOnClickListners();
    }

    private void setupOnClickListners(){
        pleaseTapHereTextView.setOnClickListener(view -> whyActivityIntent());
        saveButton.setOnClickListener(view -> saveButtonClicked());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userInfoPresenter.onDestory();
    }

    private void whyActivityIntent() {
        Intent whyIntent = new Intent(this, WhyActivity.class);
        startActivity(whyIntent);
    }


    private void saveButtonClicked(){
        userInfoPresenter.createBabblePlayer(createBabblePlayer());
        userInfoPresenter.updatePlayer();

    }


    private BabblePlayer createBabblePlayer(){
        BabblePlayer babblePlayer = new BabblePlayer();
        babblePlayer.setBabyName(firstNameEditText.getText().toString().trim());
        babblePlayer.setZipCode(Integer.parseInt(zipCodeEditText.getText().toString().trim()));
        babblePlayer.setBabyBirthday(birthDayEditText.getText().toString().trim());
        babblePlayer.setBabbleID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        return babblePlayer;
    }



    @Override
    public void displayErrorDialog(int dialogTitle, int dialogMessage) {
        this.runOnUiThread(() -> Utility.displayAlertMessage(dialogTitle,dialogMessage,this));

    }

    @Override
    public void dismissActivity() {
        UserInfoActivity.this.finish();
    }

    @Override
    public void downloadIntent() {
        Intent downloadIntent = new Intent(UserInfoActivity.this,DownloadActivity.class);
        startActivity(downloadIntent);
    }

    @Override
    public void displayUserInfo(BabblePlayer babblePlayer) {
        birthDayEditText.setText(babblePlayer.getBabyBirthday());
        firstNameEditText.setText(babblePlayer.getBabyName());
        zipCodeEditText.setText(String.valueOf(babblePlayer.getZipCode()));
    }

    @Override
    public void displaySaveDialog() {
        this.runOnUiThread(() -> createDialog() );
    }

    private void createDialog(){
        mDialog = new ProgressDialog(UserInfoActivity.this);
        mDialog.setMessage("Saving Player Info");
        mDialog.setTitle("Beginning to Babble");
        mDialog.show();
    }

    @Override
    public void dismissSaveDialog() {
        this.runOnUiThread(() -> mDialog.dismiss());
    }
}
