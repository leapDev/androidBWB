package com.example.ryangunn.beginningtobabble;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ryangunn.beginningtobabble.library.LibarayFragment;

public class DetailActivity extends AppCompatActivity  {

    public static final String DETAIL_INTENT = "detailIntent";
    public static final int PLAY_TODAY = 1;
    public static final int LIBRARY = 2;
    public static final int SETTING = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        int fragmentToDisplay = getIntent().getIntExtra(DETAIL_INTENT,1);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.detailFragment, getSelectedFragment(fragmentToDisplay));

        ft.commit();

    }

    private Fragment getSelectedFragment(int fragmentToDisplay){
        if (fragmentToDisplay == PLAY_TODAY){
            return new PlayTodayFragment();
        }else if (fragmentToDisplay == LIBRARY){
            return new LibarayFragment();
        }else {
            return null;
        }
    }
}
