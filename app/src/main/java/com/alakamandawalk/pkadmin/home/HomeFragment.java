package com.alakamandawalk.pkadmin.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.category.CategoryActivity;
import com.alakamandawalk.pkadmin.category.CategoryAdapter;
import com.alakamandawalk.pkadmin.category.SimpleCategoryAdapter;
import com.alakamandawalk.pkadmin.model.CategoryData;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.alakamandawalk.pkadmin.story.StoryAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    RecyclerView storyRv, categoryRv, simpleCategoryRv;
    TextView storyCountTv, sortStoriesTv, seeAllCategoriesTv;
    StoryAdapter storyAdapter;
    List<StoryData> storyList;
    CategoryAdapter categoryAdapter;
    SimpleCategoryAdapter simpleCategoryAdapter;
    List<CategoryData> categoryList;
    ProgressDialog pd;
    private boolean showHide = false;

    private AdView mAdView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        storyRv = view.findViewById(R.id.storyRv);
        categoryRv = view.findViewById(R.id.categoryRv);
        seeAllCategoriesTv = view.findViewById(R.id.seeAllCategoriesTv);
        sortStoriesTv = view.findViewById(R.id.sortStoriesTv);
        storyCountTv = view.findViewById(R.id.storyCountTv);
        simpleCategoryRv = view.findViewById(R.id.simpleCategoryRv);
        pd = new ProgressDialog(getActivity());
        seeAllCategoriesTv.setText("SHOW ALL");

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

        LinearLayoutManager categoryLayoutManager =
                new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        true);
        categoryLayoutManager.setStackFromEnd(true);
        categoryRv.setLayoutManager(categoryLayoutManager);


        LinearLayoutManager storyLayoutManager = new LinearLayoutManager(getActivity());
        storyLayoutManager.setStackFromEnd(true);
        storyLayoutManager.setReverseLayout(true);
        storyRv.setLayoutManager(storyLayoutManager);

        LinearLayoutManager simpleCategoryLayoutManager = new LinearLayoutManager(getActivity());
        simpleCategoryLayoutManager.setStackFromEnd(true);
        simpleCategoryLayoutManager.setReverseLayout(true);
        simpleCategoryRv.setLayoutManager(simpleCategoryLayoutManager);
        simpleCategoryRv.setVisibility(View.GONE);


        categoryList = new ArrayList<>();
        storyList = new ArrayList<>();

        checkNetworkStatus();

        seeAllCategoriesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showHide){
                    showHide = false;
                    simpleCategoryRv.setVisibility(View.GONE);
                    categoryRv.setVisibility(View.VISIBLE);
                    seeAllCategoriesTv.setText("SHOW ALL");

                }else {
                    showHide = true;
                    simpleCategoryRv.setVisibility(View.VISIBLE);
                    categoryRv.setVisibility(View.GONE);
                    seeAllCategoriesTv.setText("SHOW LESS");
                }
            }
        });

        sortStoriesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(sortStoriesTv);
                getActivity().openContextMenu(v);
            }
        });

        return view;
    }

    private void loadStories(final String sort) {

        if (sort.equals("shuffle")){

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    storyList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        StoryData storyData = ds.getValue(StoryData.class);
                        storyList.add(storyData);
                        Collections.shuffle(storyList);
                        storyAdapter = new StoryAdapter(getActivity(), storyList);
                        storyRv.setAdapter(storyAdapter);
                    }
                    storyCountTv.setText(storyList.size()+" STORIES");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (sort.equals("byDateAsc")){

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    storyList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        StoryData storyData = ds.getValue(StoryData.class);
                        storyList.add(storyData);
                        storyAdapter = new StoryAdapter(getActivity(), storyList);
                        storyRv.setAdapter(storyAdapter);
                    }
                    storyCountTv.setText(storyList.size()+" STORIES");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (sort.equals("byDateDsc")){

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    storyList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        StoryData storyData = ds.getValue(StoryData.class);
                        storyList.add(storyData);
                        Collections.reverse(storyList);
                        storyAdapter = new StoryAdapter(getActivity(), storyList);
                        storyRv.setAdapter(storyAdapter);
                    }
                    storyCountTv.setText(storyList.size()+" STORIES");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadCategories() {

        pd.setMessage("Loading...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("category");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    CategoryData categoryData = ds.getValue(CategoryData.class);

                    categoryList.add(categoryData);
                    Collections.shuffle(categoryList);
                    categoryAdapter = new CategoryAdapter(getActivity(), categoryList);
                    simpleCategoryAdapter = new SimpleCategoryAdapter(getActivity(), categoryList);
                    categoryRv.setAdapter(categoryAdapter);
                    simpleCategoryRv.setAdapter(simpleCategoryAdapter);

                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    public void checkNetworkStatus(){

        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
            loadCategories();
            loadStories("shuffle");
        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

        }

    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_sort_stories,menu);
        menu.setHeaderTitle("Sort By");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.sort_shuffle:
                loadStories("shuffle");
                break;

            case R.id.sort_by_date_asc:
                loadStories("byDateAsc");
                break;

            case R.id.sort_by_date_dsc:
                loadStories("byDateDsc");
                break;

        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        showHide = false;
        simpleCategoryRv.setVisibility(View.GONE);
        categoryRv.setVisibility(View.VISIBLE);
        seeAllCategoriesTv.setText("SHOW ALL");
    }
}
