package com.leap.ryangunn.beginningtobabble.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leap.ryangunn.beginningtobabble.Constant;
import com.leap.ryangunn.beginningtobabble.DetailActivity;
import com.leap.ryangunn.beginningtobabble.HomeActivity;
import com.leap.ryangunn.beginningtobabble.R;
import com.leap.ryangunn.beginningtobabble.Utility;
import com.leap.ryangunn.beginningtobabble.helper.AnswerNotification;
import com.leap.ryangunn.beginningtobabble.models.TipReminder;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by ryangunn on 9/13/16.
 */
public class TipSettingsFragment extends Fragment {
    ArrayList<TipReminder> mUpdatedTipReminder = new ArrayList<>();
    ArrayList<TipReminder> mTipReminders = new ArrayList<TipReminder>();
    Realm mRealm = Realm.getDefaultInstance();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tip_settings,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSaveButton = (Button)view.findViewById(R.id.tipSettingSaveButton);
        mSaveButton.setVisibility(View.INVISIBLE);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.writeIntSharedPreferences(Constant.START_TIME,firstTipIndex,getActivity());
                Utility.writeIntSharedPreferences(Constant.END_TIME,secondTipTimeIndex,getActivity());
               deleteAnswerNotifications();
                Utility.addCustomEvent(Constant.CHANGED_NOTIFICATION_SETTINGS);
                getActivity().finish();
            }
        });

        firstTipIndex = Utility.readIntSharedPreferences(Constant.START_TIME,getActivity());
        secondTipTimeIndex = Utility.readIntSharedPreferences(Constant.END_TIME,getActivity());


        startTime = getResources().getStringArray(R.array.first_tip_settings_array);
        endTime = getResources().getStringArray(R.array.second_tips_settings_array);

        mPlusButton = (Button)view.findViewById(R.id.tipSettingsMaxTipNumberPlusButton);
        mMinusButton = (Button)view.findViewById(R.id.tipsettingsMaxNumberTipMinusButton);
        mSecondTipBoxMinusButton = (Button)view.findViewById(R.id.tipsettingsSecondTipMinusButton);

        mSecondTipBoxPlusButton = (Button)view.findViewById(R.id.tipSettingsSecondTipPlusButton);
        mSecondTipTextView = (TextView) view.findViewById(R.id.tipSettingsSecondTipTextView);

        mFirstTipBoxMinusButton = (Button)view.findViewById(R.id.tipSettingsStartingMinusButton);
        mFirstTipBoxPlusButton = (Button)view.findViewById(R.id.tipSettingsStartingPlusButton);

        mFirstTipTextView = (TextView) view.findViewById(R.id.tipSettingsStartingTextView);

        mFirstTipTextView.setText(startTime[firstTipIndex]);
        mSecondTipTextView.setText(endTime[secondTipTimeIndex]);


        mNumberofTipsTextView = (TextView)view.findViewById(R.id.tipSettingsMaxNumberTipTextView);
        mNumberofTipsTextView.setText(maxTips[0]);
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (maxNumberOfTipIndex != 7){
                   updateTextView(true);
                }
            }
        });

        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (maxNumberOfTipIndex != 0) {
                    updateTextView(false);
                }
            }
        });

        ImageView libraryImageView = (ImageView)view.findViewById(R.id.tipSettingsFragmentLibararyImageView);
        ImageView homeImageView = (ImageView)view.findViewById(R.id.tipSettingsFragmentHomeImageView);
        ImageView playToday = (ImageView)view.findViewById(R.id.tipSettingsFragmentPlayTodayImageView);

        libraryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent libraryIntent = new Intent(getActivity(), DetailActivity.class);
                libraryIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.LIBRARY);
                getActivity().startActivity(libraryIntent);

            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(getActivity(), HomeActivity.class);

                getActivity().startActivity(homeIntent);
            }
        });

        playToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playTodayIntent = new Intent(getActivity(), DetailActivity.class);
                playTodayIntent.putExtra(DetailActivity.DETAIL_INTENT,DetailActivity.PLAY_TODAY);
                getActivity().startActivity(playTodayIntent);
            }
        });

        setOnClickListnerForFirstBox();
        setOnClickListnerSecondBox();

    }

    private void setOnClickListnerForFirstBox(){
        mFirstTipBoxPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFirstTipBox(true);
            }
        });

        mFirstTipBoxMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFirstTipBox(false);
            }
        });
    }

    private void setOnClickListnerSecondBox(){
        mSecondTipBoxMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSecondTipBox(false);
            }
        });

        mSecondTipBoxPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSecondTipBox(true);
            }
        });
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
        realm.where(AnswerNotification.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<AnswerNotification>>() {
            @Override
            public void onChange(final RealmResults<AnswerNotification> element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        element.deleteAllFromRealm();
                    }
                });
            }
        });
    }


}
