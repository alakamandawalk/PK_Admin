package com.alakamandawalk.pkadmin.download;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alakamandawalk.pkadmin.SettingsActivity;
import com.alakamandawalk.pkadmin.localdb.DBHelper;
import com.alakamandawalk.pkadmin.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment {

    DBHelper dbHelper;

    RecyclerView favStoryRv;
    DownloadedStoryAdapter downloadedStoryAdapter;

    public DownloadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        checkNightModeActivated();

        favStoryRv = view.findViewById(R.id.favStoryRv);

        dbHelper = new DBHelper(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        favStoryRv.setLayoutManager(layoutManager);

        loadStories();

        return view;
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

    private void loadStories() {

        downloadedStoryAdapter = new DownloadedStoryAdapter(getActivity(), dbHelper.getAllStories());
        favStoryRv.setAdapter(downloadedStoryAdapter);

    }

    @Override
    public void onResume() {
        loadStories();
        checkNightModeActivated();
        super.onResume();
    }
}
