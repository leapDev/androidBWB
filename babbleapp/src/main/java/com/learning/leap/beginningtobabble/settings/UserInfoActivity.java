package com.learning.leap.beginningtobabble.settings;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.learning.leap.beginningtobabble.R;

public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Boolean newUser = getIntent().getBooleanExtra("newUser",false);
        ft.replace(R.id.userInfoActivityFragment, UserInfoFragment.newInstance(newUser));
        ft.commit();

    }
}