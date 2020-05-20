package com.alakamandawalk.pkadmin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsFragment extends PreferenceFragment {

    SwitchPreference enableDarkMode;

    public static final String THEME_PREFERENCE = "nightModePref";
    public static final String KEY_IS_NIGHT_MODE = "isNightMode";
    SharedPreferences themePreference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        themePreference = getActivity().getSharedPreferences(THEME_PREFERENCE, Context.MODE_PRIVATE);

        enableDarkMode = (android.preference.SwitchPreference) findPreference("enable_dark_mode");

        enableDarkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (enableDarkMode.isChecked()){

                    ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveNightModeState(false);

                }else {

                    ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(true);

                }
                return true;
            }
        });

        checkNightModeActivated();

    }

    public void checkNightModeActivated() {

        if (themePreference.getBoolean(KEY_IS_NIGHT_MODE, false)){
            ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void saveNightModeState(boolean b) {

        SharedPreferences.Editor editor = themePreference.edit();
        editor.putBoolean(KEY_IS_NIGHT_MODE, b);
        editor.apply();

    }
}
