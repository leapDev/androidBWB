package com.learning.leap.bwb.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.learning.leap.bwb.BuildConfig;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.baseActivity.DetailActivity;
import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.helper.AnswerNotification;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class TipSettingsFragment extends Fragment {
    Button mSaveButton;
    String[] maxTips = {"3","4","5","6","7","8","9","10"};
    String[] startTime;
    String[] endTime;
    int maxNumberOfTipIndex = 0;
    int firstTipIndex = 0;
    int secondTipTimeIndex = 0;
    TextView mNumberofTipsTextView;
    TextView mFirstTipTextView;
    TextView mSecondTipTextView;
    Button mPlusButton;
    Button mMinusButton;
    Button mFirstTipBoxPlusButton;
    Button mFirstTipBoxMinusButton;
    Button mSecondTipBoxPlusButton;
    Button mSecondTipBoxMinusButton;
    Boolean turnOffTips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tip_settings,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            turnOffTips = Utility.readBoolSharedPreferences(Constant.SEND_TIPS_TODAY, getActivity());
        mSaveButton = (Button)view.findViewById(R.id.tipSettingSaveButton);
        mSaveButton.setVisibility(View.INVISIBLE);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.writeIntSharedPreferences(Constant.START_TIME,firstTipIndex,getActivity());
                Utility.writeIntSharedPreferences(Constant.END_TIME,secondTipTimeIndex,getActivity());
                String userMaxTip = maxTips[maxNumberOfTipIndex];
                int userMaxTipInt = Integer.parseInt(userMaxTip);
                Utility.writeIntSharedPreferences(Constant.NUM_OF_TIPS_INDEX,maxNumberOfTipIndex,getActivity());
                Utility.writeIntSharedPreferences(Constant.NUM_OF_TIPS,userMaxTipInt,getActivity());
               deleteAnswerNotifications();
                Utility.addCustomEvent(Constant.CHANGED_NOTIFICATION_SETTINGS,Utility.getUserID(getActivity()),null);
                setTipRemdinder();
                getActivity().finish();
            }
        });

        firstTipIndex = Utility.readIntSharedPreferences(Constant.START_TIME,getActivity());
        secondTipTimeIndex = Utility.readIntSharedPreferences(Constant.END_TIME,getActivity());


        startTime = getResources().getStringArray(R.array.start_times_settings_array);
        endTime = getResources().getStringArray(R.array.end_times_tips_settings_array);

        mPlusButton = (Button)view.findViewById(R.id.tipSettingsMaxTipNumberPlusButton);
        mMinusButton = (Button)view.findViewById(R.id.tipsettingsMaxNumberTipMinusButton);
        mSecondTipBoxMinusButton = (Button)view.findViewById(R.id.tipSettingsEndTimeMinusButton);

        mSecondTipBoxPlusButton = (Button)view.findViewById(R.id.tipSettingsEndTimePlusButton);
        mSecondTipTextView = (TextView) view.findViewById(R.id.tipSettingsEndTimeTextView);

        mFirstTipBoxMinusButton = (Button)view.findViewById(R.id.tipSettingsStartTimeMinusButton);
        mFirstTipBoxPlusButton = (Button)view.findViewById(R.id.tipSettingsStartTimePlusButton);

        mFirstTipTextView = (TextView) view.findViewById(R.id.tipSettingsStartTimeTextView);

        mFirstTipTextView.setText(startTime[firstTipIndex]);
        mSecondTipTextView.setText(endTime[secondTipTimeIndex]);
        mNumberofTipsTextView = (TextView)view.findViewById(R.id.tipSettingsMaxNumberTipTextView);
        int numberOfUserTips = Utility.readIntSharedPreferences(Constant.NUM_OF_TIPS_INDEX,getActivity());
        mNumberofTipsTextView.setText(maxTips[numberOfUserTips]);
        mPlusButton.setOnClickListener(view15 -> {
            if (maxNumberOfTipIndex != 7){
               updateTextView(true);
            }
        });

        mMinusButton.setOnClickListener(view1 -> {
            if (maxNumberOfTipIndex != 0) {
                updateTextView(false);
            }
        });

        Switch turnOffSwitch = (Switch)view.findViewById(R.id.tipSettingsFragmentSwitch);
        turnOffSwitch.setChecked(turnOffTips);
        turnOffSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            displaySaveButton();
            turnOffTips = b;
        });

        if (!BuildConfig.FLAVOR.equals("regular")) {
            mNumberofTipsTextView.setVisibility(View.GONE);
            mPlusButton.setVisibility(View.GONE);
            mMinusButton.setVisibility(View.GONE);
            turnOffSwitch.setVisibility(View.GONE);
            TextView maxTips =  (TextView)view.findViewById(R.id.maxTipsTextView);
            maxTips.setVisibility(View.GONE);

        }

        ImageView libraryImageView = (ImageView)view.findViewById(R.id.tipSettingsFragmentLibararyImageView);
        ImageView homeImageView = (ImageView)view.findViewById(R.id.tipSettingsFragmentHomeImageView);
        ImageView playToday = (ImageView)view.findViewById(R.id.tipSettingsFragmentPlayTodayImageView);
        Utility.hideButtonCheck(libraryImageView,playToday);

        libraryImageView.setOnClickListener(view12 -> {
            Intent libraryIntent = new Intent(getActivity(), DetailActivity.class);
            libraryIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
            getActivity().startActivity(libraryIntent);

        });

        homeImageView.setOnClickListener(view13 -> {
            Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
            getActivity().startActivity(homeIntent);
        });

        playToday.setOnClickListener(view14 -> {
            Intent playTodayIntent = new Intent(getActivity(), DetailActivity.class);
            playTodayIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
            getActivity().startActivity(playTodayIntent);
        });

        setOnClickListnerForFirstBox();
        setOnClickListnerSecondBox();


    }

    private void setOnClickListnerForFirstBox(){
        mFirstTipBoxPlusButton.setOnClickListener(view -> updateFirstTipBox(true));

        mFirstTipBoxMinusButton.setOnClickListener(view -> updateFirstTipBox(false));
    }

    private void setOnClickListnerSecondBox(){
        mSecondTipBoxMinusButton.setOnClickListener(view -> updateSecondTipBox(false));

        mSecondTipBoxPlusButton.setOnClickListener(view -> updateSecondTipBox(true));
    }

    private void updateFirstTipBox(Boolean plusSignTapped){
        displaySaveButton();
        if (plusSignTapped){
            firstTipIndex++;
            if (firstTipIndex> secondTipTimeIndex){
                secondTipTimeIndex = firstTipIndex;
                mSecondTipTextView.setText(endTime[secondTipTimeIndex]);
            }
            mFirstTipTextView.setText(startTime[firstTipIndex]);

        }else {
            firstTipIndex--;
            mFirstTipTextView.setText(startTime[firstTipIndex]);
        }

        hideButton(firstTipIndex,(startTime.length-1),mFirstTipBoxPlusButton,mFirstTipBoxMinusButton);
    }

    private void updateSecondTipBox(Boolean plusSignTapped){
        displaySaveButton();
        if (plusSignTapped){
            secondTipTimeIndex++;
            mSecondTipTextView.setText(endTime[secondTipTimeIndex]);
        }else {
            secondTipTimeIndex--;
            if (firstTipIndex>secondTipTimeIndex){
                firstTipIndex = secondTipTimeIndex;
                mFirstTipTextView.setText(startTime[firstTipIndex]);
            }
            mSecondTipTextView.setText(endTime[secondTipTimeIndex]);
        }
        hideButton(secondTipTimeIndex,(endTime.length-1),mSecondTipBoxPlusButton,mSecondTipBoxMinusButton);

    }

    private void hideButton(int index,int max, Button plusButton, Button minusButton){
        if (index == max){
            plusButton.setEnabled(false);
        }else {
            plusButton.setEnabled(true);
        }

        if (index == 0){
            minusButton.setEnabled(false);
        }else {
            minusButton.setEnabled(true);
        }
    }
    public void updateTextView(Boolean plus){
        displaySaveButton();
        if (plus) {
            maxNumberOfTipIndex++;
            mNumberofTipsTextView.setText(maxTips[maxNumberOfTipIndex]);
        }else {
                maxNumberOfTipIndex--;
                mNumberofTipsTextView.setText(maxTips[maxNumberOfTipIndex]);
        }

        hideButton(maxNumberOfTipIndex,7,mPlusButton,mMinusButton);

    }

    public void displaySaveButton(){
        if (mSaveButton.getVisibility() == View.INVISIBLE){
            mSaveButton.setVisibility(View.VISIBLE);
        }
    }

    private void deleteAnswerNotifications(){
        final Realm realm = Realm.getDefaultInstance();
        realm.where(AnswerNotification.class).findAllAsync().addChangeListener(element -> realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm1) {
                element.deleteAllFromRealm();
            }
        }));
    }

    private void setTipRemdinder(){
      //  if (turnOffTips){
//            TipReminder.cancelRepeatingIntentService(getActivity());
//        }else {
//            TipReminder.cancelRepeatingIntentService(getActivity());
//            TipReminder.setUpRepeatingIntentService(getActivity());
//        }
//        Utility.writeBoolenSharedPreferences(Constant.SEND_TIPS_TODAY,turnOffTips,getActivity());
    }


}
