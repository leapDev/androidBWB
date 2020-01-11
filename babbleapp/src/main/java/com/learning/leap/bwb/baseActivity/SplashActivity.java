package com.learning.leap.bwb.baseActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Utility;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        Utility.newUserCheck(this);
    }

}
