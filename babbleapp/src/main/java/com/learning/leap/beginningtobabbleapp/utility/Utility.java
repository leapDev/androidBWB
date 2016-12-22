package com.learning.leap.beginningtobabbleapp.utility;


import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.learning.leap.beginningtobabbleapp.R;
import com.learning.leap.beginningtobabbleapp.tipReminder.SetNotficationsBroadcastReciver;

public class Utility {
    private static final String sharedPreferencesFile = "Global";


    public  static CognitoCredentialsProvider getCredientail(Context context){
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),  /* get the context for the application */
                Constant.CognitoIdentityPoolId,    /* Identity Pool ID */
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        return credentialsProvider;
    }
    public static void displayAlertMessage(int errorTitle, int errorMessage,Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(errorTitle);
        builder.setMessage(errorMessage);
        builder.setNeutralButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    public static void writeIntSharedPreferences(String sharedPreferenceKey, int intToWrite,Context context){
        SharedPreferences mSharedPreferences;

        mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(sharedPreferenceKey,intToWrite);
        editor.apply();

    }
    public static void writeStringSharedPreferences(String sharedPreferenceKey, String stringToWrite,Context context){
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(sharedPreferenceKey, stringToWrite);
        editor.apply();

    }

    public static void writeBoolenSharedPreferences(String sharedPreferenceKey, Boolean boolToWrite,Context context){
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(sharedPreferenceKey, boolToWrite);
        editor.apply();

    }


    public static int readIntSharedPreferences(String sharedPreferenceKey,Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_APPEND);
        return mSharedPreferences.getInt(sharedPreferenceKey,0);
    }

    public static String readStringSharedPreferences(String sharedPreferenceKey,Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_APPEND);
        return mSharedPreferences.getString(sharedPreferenceKey, null);
    }

    public static Boolean readBoolSharedPreferences(String sharedPreferenceKey,Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_APPEND);
        return mSharedPreferences.getBoolean(sharedPreferenceKey,false);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean connectedWif(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return  (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static void addCustomEvent(String event){
        Answers.getInstance().logCustom(new CustomEvent(event));
    }

    public static void addCustomEventWithNotification(String event,String notificationString){
        Answers.getInstance().logCustom(new CustomEvent(event).putCustomAttribute("Notification",notificationString));
    }

    public static PendingIntent repeatingAlarmPendingIntent(Context context){
        Intent intent = new Intent(context, SetNotficationsBroadcastReciver.class);
         return PendingIntent.getBroadcast(context,10,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
