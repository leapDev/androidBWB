package com.example.ryangunn.beginningtobabble.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ryangunn.beginningtobabble.DetailActivity;
import com.example.ryangunn.beginningtobabble.HomeActivity;
import com.example.ryangunn.beginningtobabble.R;


public class SettingOptionFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_home_page,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button tipSettings = (Button)view.findViewById(R.id.settingsFragmentTipSettingButton);
        Button profileSettings = (Button)view.findViewById(R.id.settingsFragmentProfileSettingsButton);

    }
}
