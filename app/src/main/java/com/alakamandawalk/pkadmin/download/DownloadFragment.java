package com.alakamandawalk.pkadmin.download;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alakamandawalk.pkadmin.localdb.DBHelper;
import com.alakamandawalk.pkadmin.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment {

    DBHelper dbHelper;

    RecyclerView favStoryRv;
    DownloadedStoryAdapter downloadedStoryAdapter;

    private AdView mAdView;

    public DownloadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        favStoryRv = view.findViewById(R.id.favStoryRv);

        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544~3347511713");

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dbHelper = new DBHelper(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        favStoryRv.setLayoutManager(layoutManager);

        loadStories();

        return view;
    }

    private void loadStories() {

        downloadedStoryAdapter = new DownloadedStoryAdapter(getActivity(), dbHelper.getAllStories());
        favStoryRv.setAdapter(downloadedStoryAdapter);

    }

    @Override
    public void onResume() {
        loadStories();
        super.onResume();
    }
}
