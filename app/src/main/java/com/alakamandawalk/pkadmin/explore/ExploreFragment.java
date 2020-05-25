package com.alakamandawalk.pkadmin.explore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.SettingsActivity;
import com.alakamandawalk.pkadmin.author.AuthorActivity;
import com.alakamandawalk.pkadmin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    ImageButton authorsIb, requestStoryIb, rateUsIb, likeUsFBIb, otherAppsIb, aboutUsIb;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        checkNightModeActivated();

        authorsIb = view.findViewById(R.id.authorsIb);
        requestStoryIb = view.findViewById(R.id.requestStoryIb);
        rateUsIb = view.findViewById(R.id.rateUsIb);
        likeUsFBIb = view.findViewById(R.id.likeUsFBIb);
        otherAppsIb = view.findViewById(R.id.otherAppsIb);
        aboutUsIb = view.findViewById(R.id.aboutUsIb);

        likeUsFBIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent faceBookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/meedumpaaru"));
                startActivity(faceBookIntent);
            }
        });

        requestStoryIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailReq();
            }
        });

        authorsIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AuthorActivity.class));
            }
        });

        return view;
    }

    private void sendEmailReq() {

        String[] TO = {"kasundularavau@gmail.com"};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }catch (android.content.ActivityNotFoundException e){
            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void checkNightModeActivated() {

        SharedPreferences themePref = getActivity().getSharedPreferences(SettingsActivity.THEME_PREFERENCE, Context.MODE_PRIVATE);
        boolean isDarkMode = themePref.getBoolean(SettingsActivity.KEY_IS_NIGHT_MODE, false);

        if (isDarkMode){
            ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onResume() {
        checkNightModeActivated();
        super.onResume();
    }
}
