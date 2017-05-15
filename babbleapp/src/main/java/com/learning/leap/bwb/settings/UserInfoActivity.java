package com.learning.leap.bwb.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.learning.leap.bwb.download.DownloadActivity;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.userInfo.UserInfoPresenter;
import com.learning.leap.bwb.userInfo.UserInfoViewInterface;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.NetworkChecker;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.models.Notification;

import java.util.List;

import io.realm.Realm;

public class UserInfoActivity extends AppCompatActivity implements UserInfoViewInterface {
    EditText birthDayEditText;
   EditText zipCodeEditText;
     EditText firstNameEditText;
    Button saveButton;
    TextView pleaseTapHereTextView;
    Boolean newUser;
    ProgressDialog mDialog;
    UserInfoPresenter userInfoPresenter;
    RadioButton maleRadioButton;
    RadioButton femaleRadioButton;
    RadioButton notNowRadioButton;
    String gender;
    NetworkChecker checker;
    LocalLoadSaveHelper saveHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);
        setUpViews();
        newUser = getIntent().getBooleanExtra(Constant.NEW_USER,false);
         saveHelper = new LocalLoadSaveHelper(this);
         checker = new NetworkChecker(this);
        AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient(Utility.getCredientail(this));
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
        userInfoPresenter = new UserInfoPresenter(newUser,this,saveHelper,checker,Realm.getDefaultInstance(),mapper);
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
        maleRadioButton = (RadioButton)findViewById(R.id.maleRadioButton);
        femaleRadioButton = (RadioButton)findViewById(R.id.femaleRadioButton);
        notNowRadioButton = (RadioButton)findViewById(R.id.notNowRadioButton);
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
        userInfoPresenter.checkUserInput();

    }

    private void setGender(){
        if (maleRadioButton.isChecked()){
            gender ="Male";
        }else if (femaleRadioButton.isChecked()){
            gender = "Female";
        }else {
            gender = "Not Now";
        }
    }


    private BabblePlayer createBabblePlayer(){
        BabblePlayer babblePlayer = new BabblePlayer();
        babblePlayer.setBabyName(firstNameEditText.getText().toString().trim());
        babblePlayer.setZipCode(setZipCode());
        babblePlayer.setBabyBirthday(birthDayEditText.getText().toString().trim());
        babblePlayer.setBabbleID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        setGender();
        babblePlayer.setBabyGender(gender);
        return babblePlayer;
    }

    private int setZipCode(){
        try {
           return Integer.parseInt(zipCodeEditText.getText().toString().trim());
        }catch (NumberFormatException e){
            return 0;
        }
    }

    @Override
    public void displayErrorDialog(int dialogTitle, int dialogMessage) {
        this.runOnUiThread(() -> Utility.displayAlertMessage(dialogTitle,dialogMessage,this));

    }

    @Override
    public void dismissActivity() {
        Utility.addCustomEvent(Constant.CHANGE_PROFILE_SETTINGS,Utility.getUserID(this),null);
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
        switch (babblePlayer.getBabyGender()) {
            case "Male":
                maleRadioButton.setChecked(true);
                break;
            case "Female":
                femaleRadioButton.setChecked(true);
                break;
            default:
                notNowRadioButton.setChecked(true);
                break;
        }
    }

    @Override
    public String getGender() {
        setGender();
        return gender;
    }

    @Override
    public void displaySaveDialog() {
        this.runOnUiThread(this::createDialog);
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
