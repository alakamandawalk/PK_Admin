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


        dbHelper = new DBHelper(getActivity());

        favStoryRv = view.findViewById(R.id.favStoryRv);

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
