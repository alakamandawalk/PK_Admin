package com.alakamandawalk.pkadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    ImageButton backIb;
    Switch darkModeSw;

    public static final String THEME_PREFERENCE = "nightModePref";
    public static final String KEY_IS_NIGHT_MODE = "isNightMode";
    SharedPreferences themePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backIb = findViewById(R.id.backIb);
        darkModeSw = findViewById(R.id.darkModeSw);

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        themePreference = getSharedPreferences(THEME_PREFERENCE, Context.MODE_PRIVATE);

        darkModeSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (darkModeSw.isChecked()){
                    saveNightModeState(true);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                }else {
                    saveNightModeState(false);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        checkNightModeActivated();
    }

    private void saveNightModeState(boolean b) {

        SharedPreferences.Editor editor = themePreference.edit();
        editor.putBoolean(KEY_IS_NIGHT_MODE, b);
        editor.apply();
    }

    public void checkNightModeActivated() {

        if (themePreference.getBoolean(KEY_IS_NIGHT_MODE, false)){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkModeSw.setChecked(true);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            darkModeSw.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
