package com.learning.leap.bwb.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.learning.leap.bwb.BuildConfig;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.baseActivity.DetailActivity;
import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.tipSettings.TipSettingsPresenters;
import com.learning.leap.bwb.tipSettings.TipSettingsViewInterface;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

public class TipSettingsActivity extends AppCompatActivity implements TipSettingsViewInterface {
    TextView numberofTipsTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;
    Button maxNumberOfTipsPlusButton;
    Button maxNumberOfTipsMinusButton;
    Button startTimePlusButton;
    Button startTimeMinusButton;
    Button endTimePlusButton;
    Button endTimeMinusButton;
    Button saveButton;
    Switch sendTipsTodaySwitch;
    TipSettingsPresenters tipSettingsPresenters;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tip_settings);
        setUpBackground();
        setUpViews();
        setBottomButtonViews();
        tipSettingsPresenters = new TipSettingsPresenters(this,this);
        tipSettingsPresenters.onCreate();
    }

    private void setUpViews(){
        saveButton = (Button)findViewById(R.id.tipSettingSaveButton);
        sendTipsTodaySwitch = (Switch)findViewById(R.id.tipSettingsFragmentSwitch);
        if (!BuildConfig.FLAVOR.equals("regular")) {
            sendTipsTodaySwitch.setVisibility(View.GONE);
        }
        setUpPlusAndMinusButtons();
        setUpTextViews();
        setOnClickListners();
    }

    private void setUpBackground(){
        ImageView background = (ImageView)findViewById(R.id.tipSettingsBackground);
        Bitmap backgroundBitmap = Utility.decodeSampledBitmapFromResource(getResources(),R.drawable.settings_bg,Utility.getDisplayMetrics(this));
        background.setImageBitmap(backgroundBitmap);
    }
    private void setUpPlusAndMinusButtons(){
        maxNumberOfTipsPlusButton = (Button)findViewById(R.id.tipSettingsMaxTipNumberPlusButton);
        maxNumberOfTipsMinusButton = (Button)findViewById(R.id.tipsettingsMaxNumberTipMinusButton);
        if (!BuildConfig.FLAVOR.equals("regular")) {
           maxNumberOfTipsMinusButton.setVisibility(View.GONE);
            maxNumberOfTipsPlusButton.setVisibility(View.GONE);

        }
        endTimeMinusButton = (Button)findViewById(R.id.tipSettingsEndTimeMinusButton);
        endTimePlusButton = (Button)findViewById(R.id.tipSettingsEndTimePlusButton);
        startTimeMinusButton = (Button)findViewById(R.id.tipSettingsStartTimeMinusButton);
        startTimePlusButton = (Button)findViewById(R.id.tipSettingsStartTimePlusButton);
    }
    private void setUpTextViews(){
        numberofTipsTextView = (TextView)findViewById(R.id.tipSettingsMaxNumberTipTextView);
        startTimeTextView = (TextView) findViewById(R.id.tipSettingsStartTimeTextView);
        endTimeTextView = (TextView) findViewById(R.id.tipSettingsEndTimeTextView);
        if (!BuildConfig.FLAVOR.equals("regular")) {
            numberofTipsTextView.setVisibility(View.GONE);
            TextView maxTips =  (TextView)findViewById(R.id.maxTipsTextView);
            maxTips.setVisibility(View.GONE);

        }
    }

    private void setOnClickListners(){
        saveButton.setOnClickListener(view -> tipSettingsPresenters.save());
        maxNumberOfTipsPlusButton.setOnClickListener(view -> tipSettingsPresenters.userMaxTipsPlusButtonPressed());
        maxNumberOfTipsMinusButton.setOnClickListener(view -> tipSettingsPresenters.userMaxTipMinusButtonPressed());
        startTimePlusButton.setOnClickListener(view -> tipSettingsPresenters.startTimePlusButtonPressed());
        startTimeMinusButton.setOnClickListener(view -> tipSettingsPresenters.startTimeMinusButtonPressed());
        endTimeMinusButton.setOnClickListener(view -> tipSettingsPresenters.endTimeMinusButtonPressed());
        endTimePlusButton.setOnClickListener(view -> tipSettingsPresenters.endTimePlusButtonPressed());
        sendTipsTodaySwitch.setOnCheckedChangeListener((compoundButton, b) -> tipSettingsPresenters.turnOfSwitchChangedListner(b));
    }

    private void setBottomButtonViews(){
        ImageView libraryImageView = (ImageView)findViewById(R.id.tipSettingsFragmentLibararyImageView);
        ImageView homeImageView = (ImageView)findViewById(R.id.tipSettingsFragmentHomeImageView);
        ImageView playToday = (ImageView)findViewById(R.id.tipSettingsFragmentPlayTodayImageView);
        Utility.hideButtonCheck(playToday,libraryImageView);
        libraryImageView.setOnClickListener(view -> onLibraryPress());
        homeImageView.setOnClickListener(view -> onHomePress());
        playToday.setOnClickListener(view -> onPlayToday());
    }

    @Override
    public void displayStartTimeAtIndex(String startTime) {
        startTimeTextView.setText(startTime);
    }

    @Override
    public void displayEndTimeAtIndex(String endTime) {
        endTimeTextView.setText(endTime);
    }

    @Override
    public void displayUserMaxTipsAtIndex(String maxTips) {
        numberofTipsTextView.setText(maxTips);
    }

    @Override
    public void hideStartTimePlusSigned() {
        startTimePlusButton.setEnabled(false);
    }

    @Override
    public void hideStartTimeMinusSigned() {
        startTimeMinusButton.setEnabled(false);
    }

    @Override
    public void hideEndTimePlusSigned() {
        endTimePlusButton.setEnabled(false);
    }

    @Override
    public void hideEndTimeMinusSigned() {
        endTimeMinusButton.setEnabled(false);
    }

    @Override
    public void hideMaxNumberOfTipsPlusSigned() {
        maxNumberOfTipsPlusButton.setEnabled(false);
    }

    @Override
    public void hideMaxNumberOfTipsMinusSigned() {
        maxNumberOfTipsMinusButton.setEnabled(false);
    }

    @Override
    public void setTipSwitch(Boolean sendTipsToday) {
        sendTipsTodaySwitch.setChecked(sendTipsToday);
    }

    @Override
    public void displayStartTimePlusSigned() {
        startTimePlusButton.setEnabled(true);
    }

    @Override
    public void displayStartTimeMinusSigned() {
        startTimeMinusButton.setEnabled(true);
    }

    @Override
    public void displayEndTimePlusSigned() {
        endTimePlusButton.setEnabled(true);
    }

    @Override
    public void displayEndTimeMinusSigned() {
        endTimeMinusButton.setEnabled(true);
    }

    @Override
    public void displayMaxNumberOfTipsPlusSigned() {
        maxNumberOfTipsPlusButton.setEnabled(true);
    }

    @Override
    public void displayMaxNumberOfTipsMinusSigned() {
        maxNumberOfTipsMinusButton.setEnabled(true);
    }

    @Override
    public void displaySaveButton() {
        saveButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void displayErrorMessage() {
        Utility.displayAlertMessage(R.string.BabbleError,R.string.times_are_incorrect,this);
    }

    @Override
    public void saveCompleted(boolean turnOffTips) {
        if (turnOffTips){
            Utility.addCustomEvent(Constant.TURNED_OFF_NOTIFICATIONS_FOR_DAY,Utility.getUserID(this),null);
        }
        Utility.addCustomEvent(Constant.CHANGED_NOTIFICATION_SETTINGS,Utility.getUserID(this),null);
        this.finish();
    }

    @Override
    public void onHomePress() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    @Override
    public void onSettingsPress() {

    }

    @Override
    public void onPlayToday() {
        Intent playTodayIntent = new Intent(this, DetailActivity.class);
        playTodayIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
        startActivity(playTodayIntent);
    }

    @Override
    public void onLibraryPress() {
        Intent libraryIntent = new Intent(this, DetailActivity.class);
        libraryIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
        startActivity(libraryIntent);
    }
}
