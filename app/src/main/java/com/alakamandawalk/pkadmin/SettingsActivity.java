package com.alakamandawalk.pkadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    ImageButton backIb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backIb = findViewById(R.id.backIb);

        if (findViewById(R.id.settingPreferenceContainer)!=null){

            if (savedInstanceState!=null)
                return;

            getFragmentManager().beginTransaction().add(R.id.settingPreferenceContainer, new SettingsFragment()).commit();

        }

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
