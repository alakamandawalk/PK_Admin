package com.alakamandawalk.pkadmin.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.SettingsFragment;
import com.alakamandawalk.pkadmin.story.StoryAdapter;
import com.alakamandawalk.pkadmin.model.StoryData;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    ImageButton backIb;
    RecyclerView categoryListRv;
    TextView categoryTitleTv;
    SearchView searchView;

    StoryAdapter storyAdapter;
    List<StoryData> storyDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        String categoryId = intent.getStringExtra("categoryId");
        String title = intent.getStringExtra("title");

        backIb = findViewById(R.id.backIb);
        categoryListRv = findViewById(R.id.categoryListRv);
        categoryTitleTv = findViewById(R.id.categoryTitleTv);
        searchView = findViewById(R.id.searchView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        categoryListRv.setLayoutManager(layoutManager);

        storyDataList = new ArrayList<>();

        if (key.equals("showCategoryList")){

            categoryTitleTv.setText(title);
            loadCategoryList(categoryId);
            searchView.setVisibility(View.GONE);

        }else{

            categoryTitleTv.setVisibility(View.GONE);
        }

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    loadSearchList(query);
                }else {
                    storyDataList.clear();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    loadSearchList(newText);
                }else {
                    storyDataList.clear();
                }
                return false;
            }
        });

    }

    private void loadSearchList(final String searchText) {

        DatabaseReference searchRef = FirebaseDatabase.getInstance().getReference("story");
        searchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyDataList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    StoryData storyData = ds.getValue(StoryData.class);

                    if (storyData.getStoryName().contains(searchText) || storyData.getStorySearchTag().contains(searchText)){
                        storyDataList.add(storyData);
                    }

                    storyAdapter = new StoryAdapter(CategoryActivity.this, storyDataList);
                    categoryListRv.setAdapter(storyAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCategoryList(String categoryId) {

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("story");
        Query query = categoryRef.orderByChild("storyCategoryId").equalTo(categoryId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyDataList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    StoryData storyData = ds.getValue(StoryData.class);

                    storyDataList.add(storyData);
                    storyAdapter = new StoryAdapter(CategoryActivity.this, storyDataList);
                    categoryListRv.setAdapter(storyAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
