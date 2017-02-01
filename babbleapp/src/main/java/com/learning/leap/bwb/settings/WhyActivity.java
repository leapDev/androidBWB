package com.learning.leap.bwb.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.learning.leap.bwb.R;

public class WhyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_why);
        Button backToSettignsButton = (Button)findViewById(R.id.backSettingsButton);
        backToSettignsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhyActivity.this.finish();
            }
        });


    }
}
