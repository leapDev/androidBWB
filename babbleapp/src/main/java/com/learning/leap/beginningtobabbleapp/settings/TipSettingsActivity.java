package com.learning.leap.beginningtobabbleapp.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.baseActivity.DetailActivity;
import com.learning.leap.beginningtobabbleapp.baseActivity.HomeActivity;
import com.learning.leap.beginningtobabbleapp.tipSettings.TipSettingsPresenters;
import com.learning.leap.beginningtobabbleapp.tipSettings.TipSettingsViewInterface;
import com.learning.leap.beginningtobabbleapp.userInfo.UserInfoPresenter;
import com.learning.leap.beginningtobabbleapp.utility.Utility;

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
    Switch turnOffSwitch;
    TipSettingsPresenters tipSettingsPresenters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tip_settings);
        setUpViews();
        setBottomButtonViews();
        tipSettingsPresenters = new TipSettingsPresenters(this,this);
        tipSettingsPresenters.onCreate();
    }

    private void setUpViews(){
        saveButton = (Button)findViewById(R.id.tipSettingSaveButton);
        turnOffSwitch = (Switch)findViewById(R.id.tipSettingsFragmentSwitch);
        setUpPlusAndMinusButtons();
        setUpTextViews();
        setOnClickListners();
    }

    private void setUpPlusAndMinusButtons(){
        maxNumberOfTipsPlusButton = (Button)findViewById(R.id.tipSettingsMaxTipNumberPlusButton);
        maxNumberOfTipsMinusButton = (Button)findViewById(R.id.tipsettingsMaxNumberTipMinusButton);
        endTimeMinusButton = (Button)findViewById(R.id.tipSettingsEndTimeMinusButton);
        endTimePlusButton = (Button)findViewById(R.id.tipSettingsEndTimePlusButton);
        startTimeMinusButton = (Button)findViewById(R.id.tipSettingsStartTimeMinusButton);
        startTimePlusButton = (Button)findViewById(R.id.tipSettingsStartTimePlusButton);
    }
    private void setUpTextViews(){
        numberofTipsTextView = (TextView)findViewById(R.id.tipSettingsMaxNumberTipTextView);
        startTimeTextView = (TextView) findViewById(R.id.tipSettingsStartTimeTextView);
        endTimeTextView = (TextView) findViewById(R.id.tipSettingsEndTimeTextView);
    }

    private void setOnClickListners(){
        saveButton.setOnClickListener(view -> tipSettingsPresenters.save());
        maxNumberOfTipsPlusButton.setOnClickListener(view -> tipSettingsPresenters.userMaxTipsPlusButtonPressed());
        maxNumberOfTipsMinusButton.setOnClickListener(view -> tipSettingsPresenters.userMaxTipMinusButtonPressed());
        startTimePlusButton.setOnClickListener(view -> tipSettingsPresenters.startTimePlusButtonPressed());
        startTimeMinusButton.setOnClickListener(view -> tipSettingsPresenters.startTimeMinusButtonPressed());
        endTimeMinusButton.setOnClickListener(view -> tipSettingsPresenters.endTimeMinusButtonPressed());
        endTimePlusButton.setOnClickListener(view -> tipSettingsPresenters.endTimePlusButtonPressed());
        turnOffSwitch.setOnCheckedChangeListener((compoundButton, b) -> tipSettingsPresenters.turnOfSwitchChangedListner(b));
    }

    private void setBottomButtonViews(){
        ImageView libraryImageView = (ImageView)findViewById(R.id.tipSettingsFragmentLibararyImageView);
        ImageView homeImageView = (ImageView)findViewById(R.id.tipSettingsFragmentHomeImageView);
        ImageView playToday = (ImageView)findViewById(R.id.tipSettingsFragmentPlayTodayImageView);
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
    public void setTipSwitch(Boolean turnOff) {
        turnOffSwitch.setChecked(turnOff);
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
    public void saveCompleted() {
        this.finish();
    }

    @Override
    public void onHomePress() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
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
