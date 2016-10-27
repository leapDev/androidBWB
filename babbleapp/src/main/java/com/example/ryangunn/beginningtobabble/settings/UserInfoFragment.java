package com.example.ryangunn.beginningtobabble.settings;


import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.ryangunn.beginningtobabble.DownloadActivity;
import com.example.ryangunn.beginningtobabble.R;
import com.example.ryangunn.beginningtobabble.Utility;
import com.example.ryangunn.beginningtobabble.helper.LocalLoadSaveHelper;
import com.example.ryangunn.beginningtobabble.models.BabblePlayer;
import com.example.ryangunn.beginningtobabble.models.Notification;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class UserInfoFragment extends Fragment {
    Boolean newUser;
    Date userBirthday;
    ProgressDialog mDownloadProgressDialog;
    TransferUtility mTransferUtility;

    public static UserInfoFragment newInstance(Boolean newUser) {
        Bundle args = new Bundle();
        args.putBoolean("newUser",newUser);
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            AmazonS3 s3Client = new AmazonS3Client(Utility.getCredientail(getActivity()));
            mTransferUtility = new TransferUtility(s3Client, getActivity().getApplicationContext());
        newUser = getArguments().getBoolean("newUser");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       final EditText birthDayEditText = (EditText)view.findViewById(R.id.userProfileFragmentBirtdayEditText);
        final EditText nameEdittext = (EditText)view.findViewById(R.id.userProfileFragmentFirstNameEditText);
        final EditText zipCodeEditText = (EditText)view.findViewById(R.id.userProfileFragmentZipCodeEditText);
        final Button saveButton = (Button)view.findViewById(R.id.userProfileFragmentSaveButton);
        TextView pleaseTapHereTextView = (TextView)view.findViewById(R.id.pleaseTapHereTextView);

        pleaseTapHereTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whyIntent = new Intent(getActivity(),WhyActivity.class);
                startActivity(whyIntent);
            }
        });

        final UserInfoFragment userInfoFragment = this;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkDate(birthDayEditText.getText().toString())){
                    Utility.displayAlertMessage(R.string.userBirthdayErrorTitle,R.string.userBirthdayError,getActivity());
                }else if (checkNameisEmpty(nameEdittext.getText().toString())){
                    Utility.displayAlertMessage(R.string.userNameNameErrorTitle,R.string.userNameEmptyError,getActivity());
                }else if (!checkIfNameLenghtisTooLong(nameEdittext.getText().toString())){
                    Utility.displayAlertMessage(R.string.userNameNameErrorTitle,R.string.userNameToLongError,getActivity());
                }else if (!checkZipCode(zipCodeEditText.getText().toString())){
                    Utility.displayAlertMessage(R.string.userZipCodeErrorTitle,R.string.userZipCodeError,getActivity());
                }else {
                   float daysOld=  daysBetween(userBirthday)/30;
                    if (daysOld >0){
                        BabblePlayer babblePlayer = new BabblePlayer();
                        babblePlayer.setBabyName(nameEdittext.getText().toString().trim());
                        babblePlayer.setZipCode(Integer.parseInt(zipCodeEditText.getText().toString().trim()));
                        babblePlayer.setBabyBirthday(birthDayEditText.getText().toString().trim());
                        String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        babblePlayer.setBabbleID(android_id);
                        new SavePlayer(userInfoFragment,babblePlayer,daysOld,newUser).execute();

                    }else {
                        Utility.displayAlertMessage(R.string.BabbleError,R.string.userNotBornYetError,UserInfoFragment.this.getActivity());
                    }

                }
            }
        });

        if (!newUser){
            birthDayEditText.setText(LocalLoadSaveHelper.getBabyBirthDay(getActivity()),TextView.BufferType.EDITABLE);
            nameEdittext.setText(LocalLoadSaveHelper.getBabyName(getActivity()),TextView.BufferType.EDITABLE);
            zipCodeEditText.setText(String.valueOf(LocalLoadSaveHelper.getZipCode(getActivity())));

        }
    }


    public void getFilesForDownload(final List<Notification> notifications, Boolean noPrompts, final BabblePlayer babblePlayer){
        if (noPrompts){
        Utility.displayAlertMessage(R.string.BabbleError,R.string.noPromptsForUser,getActivity());
        }else {
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(getActivity()).build();
            Realm.setDefaultConfiguration(realmConfig);
            Realm realm = Realm.getDefaultInstance();
            Utility.writeIntSharedPreferences("notficationListSize",notifications.size(),getActivity());
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.copyToRealm(notifications);
                    realm.copyToRealm(babblePlayer);
                    Intent downloadIntent = new Intent(getActivity(),DownloadActivity.class);
                    startActivity(downloadIntent);
                }
            });

        }

    }


    public float daysBetween(Date birthday){
        Date date = new Date();
        return (float)( (date.getTime() - birthday.getTime()) / (1000 * 60 * 60 * 24));
    }

    private Boolean checkDate(String date){

        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
       format.setLenient(false);
        try {

            userBirthday = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    return true;
    }

    private Boolean checkZipCode(String zipCode){
       return zipCode.matches("[0-9]{5}");
    }

    private Boolean checkNameisEmpty(String name){
        return name.isEmpty();
    }

    private Boolean checkIfNameLenghtisTooLong(String name){
        return name.length() <= 20;
    }

    public static class SavePlayer extends AsyncTask<Void,Void,Boolean>{
        float mBabyAge;
        Context mContext;
        Boolean noPrompts = false;
        BabblePlayer mBabblePlayer;
        ProgressDialog mDialog;
        List<Notification> mNotifications;
        Boolean newUser = false;
        WeakReference<UserInfoFragment> mUserInfoFragment;

        public SavePlayer(UserInfoFragment userInfoFragment,BabblePlayer babblePlayer, float babyAge,Boolean newUser){
            mBabblePlayer = babblePlayer;
            mBabyAge = babyAge;
            mUserInfoFragment = new WeakReference<UserInfoFragment>(userInfoFragment);
            mContext = mUserInfoFragment.get().getActivity();
            this.newUser = newUser;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mUserInfoFragment.get() != null) {
                mDialog = new ProgressDialog(mContext);
                mDialog.setMessage("Saving Player Info");
                mDialog.setTitle("Beginning to Babble");
                mDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(Utility.getCredientail(mContext));
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                if (newUser) {
                    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                    scanExpression.setLimit(2000);
                    HashMap<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();

                    String age = Integer.toString((int) mBabyAge);
                    AttributeValue falseAttributeValue = new AttributeValue();
                    falseAttributeValue.setS("false");
                    AttributeValue ageAttributeValue = new AttributeValue();
                    ageAttributeValue.setN(age);
                    attributeValue.put(":val", ageAttributeValue);
                    attributeValue.put(":val2", falseAttributeValue);
                    scanExpression.setExpressionAttributeValues(attributeValue);
                    scanExpression.setFilterExpression("StartMonthNumber<=:val AND EndMonthNumber>=:val AND Deleted=:val2");
                    PaginatedScanList<Notification> result = mapper.scan(Notification.class, scanExpression);
                    if (result.isEmpty()) {
                        noPrompts = true;
                    } else {
                        mapper.save(mBabblePlayer);
                        mNotifications = result;
                        saveCurrentBabblePlayer();
                    }

                }else {
                    mapper.save(mBabblePlayer);
                    saveCurrentBabblePlayer();
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            if (result){
                if (mUserInfoFragment.get() != null) {
                    if (newUser) {
                        mUserInfoFragment.get().getFilesForDownload(mNotifications, noPrompts, mBabblePlayer);
                    }else {
                        mUserInfoFragment.get().getActivity().finish();
                    }
                }
            }else {
                if (mUserInfoFragment.get() != null) {
                    Utility.displayAlertMessage(R.string.BabbleError, R.string.downloadError,mContext);
                }
            }
        }

        public void saveCurrentBabblePlayer(){
            LocalLoadSaveHelper.saveBabyBirthDay(mBabblePlayer.getBabyBirthday(),mContext);
            LocalLoadSaveHelper.saveBabyName(mBabblePlayer.getBabyName(),mContext);
            LocalLoadSaveHelper.saveBabbleID(mBabblePlayer.getBabbleID(),mContext);
            LocalLoadSaveHelper.saveZipCode(mBabblePlayer.getZipCode(),mContext);
        }
    }



}

