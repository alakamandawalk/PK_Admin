package com.alakamandawalk.pkadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    DBHelper dbHelper;

    RecyclerView favStoryRv;
    FavStoryAdapter favStoryAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);


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

        favStoryAdapter = new FavStoryAdapter(getActivity(), dbHelper.getAllStories());
        favStoryRv.setAdapter(favStoryAdapter);

    }

    @Override
    public void onResume() {
        loadStories();
        super.onResume();
    }
}
