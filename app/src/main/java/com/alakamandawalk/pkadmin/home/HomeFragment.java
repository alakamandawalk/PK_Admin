package com.alakamandawalk.pkadmin.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.DashboardActivity;
import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.SettingsActivity;
import com.alakamandawalk.pkadmin.category.CategoryAdapter;
import com.alakamandawalk.pkadmin.category.SimpleCategoryAdapter;
import com.alakamandawalk.pkadmin.download.DownloadFragment;
import com.alakamandawalk.pkadmin.model.CategoryData;
import com.alakamandawalk.pkadmin.model.StoryData;
import com.alakamandawalk.pkadmin.story.StoryAdapter;
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
    RelativeLayout homeContentRl;
    LinearLayout noConnectionLl;
    TextView storyCountTv, sortStoriesTv, seeAllCategoriesTv;
    CardView categoryCv, simpleCategoryCv;
    StoryAdapter storyAdapter;
    List<StoryData> storyList;
    CategoryAdapter categoryAdapter;
    SimpleCategoryAdapter simpleCategoryAdapter;
    List<CategoryData> categoryList;
    ProgressDialog pd;
    ProgressBar homePb;
    Button readDownloadsBtn;

    private boolean showHide = false;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        checkNightModeActivated();

        categoryCv = view.findViewById(R.id.categoryCv);
        readDownloadsBtn = view.findViewById(R.id.readDownloadsBtn);
        noConnectionLl = view.findViewById(R.id.noConnectionLl);
        homeContentRl = view.findViewById(R.id.homeContentLl);
        homePb = view.findViewById(R.id.homePb);
        simpleCategoryCv = view.findViewById(R.id.simpleCategoryCv);
        storyRv = view.findViewById(R.id.storyRv);
        categoryRv = view.findViewById(R.id.categoryRv);
        seeAllCategoriesTv = view.findViewById(R.id.seeAllCategoriesTv);
        sortStoriesTv = view.findViewById(R.id.sortStoriesTv);
        storyCountTv = view.findViewById(R.id.storyCountTv);
        simpleCategoryRv = view.findViewById(R.id.simpleCategoryRv);
        pd = new ProgressDialog(getActivity());
        seeAllCategoriesTv.setText("SHOW ALL");

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
        simpleCategoryCv.setVisibility(View.GONE);


        categoryList = new ArrayList<>();
        storyList = new ArrayList<>();

        checkNetworkStatus();

        seeAllCategoriesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showHide){
                    showHide = false;
                    simpleCategoryCv.setVisibility(View.GONE);
                    categoryCv.setVisibility(View.VISIBLE);
                    seeAllCategoriesTv.setText(getResources().getString(R.string.show_all));

                }else {
                    showHide = true;
                    simpleCategoryCv.setVisibility(View.VISIBLE);
                    categoryCv.setVisibility(View.GONE);
                    seeAllCategoriesTv.setText(getResources().getString(R.string.show_less));
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

        readDownloadsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardActivity.titleTv.setText(getResources().getString(R.string.downloads));
                DownloadFragment downloadFragment = new DownloadFragment();
                FragmentTransaction ft2 = getActivity().getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.frameLayout, downloadFragment, "");
                ft2.addToBackStack(null);
                ft2.commit();
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
                    storyCountTv.setText(storyList.size()+ " " +getString(R.string.stories));
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
                    storyCountTv.setText(storyList.size()+ " " +getString(R.string.stories));
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
                    storyCountTv.setText(storyList.size()+ " " +getString(R.string.stories));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadCategories() {

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

                }
                homePb.setVisibility(View.GONE);
                homeContentRl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkNetworkStatus(){

        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
            homeContentRl.setVisibility(View.GONE);
            loadCategories();
            loadStories("byDateAsc");
            noConnectionLl.setVisibility(View.GONE);
        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

            homeContentRl.setVisibility(View.GONE);
            homePb.setVisibility(View.GONE);
            noConnectionLl.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_sort_stories,menu);
        menu.setHeaderTitle(getResources().getString(R.string.sort_by));
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
        checkNetworkStatus();
        showHide = false;
        simpleCategoryCv.setVisibility(View.GONE);
        categoryCv.setVisibility(View.VISIBLE);
        seeAllCategoriesTv.setText(getResources().getString(R.string.show_all));
        super.onResume();
    }
}
