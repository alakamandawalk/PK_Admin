 package com.alakamandawalk.pkadmin.author;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.SettingsActivity;
import com.alakamandawalk.pkadmin.model.AuthorData;
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
import java.util.List;

 public class AuthorActivity extends AppCompatActivity {

    RecyclerView authorRv;
    ImageButton backIb;

    AuthorAdapter authorAdapter;
    List<AuthorData> authorDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        SharedPreferences themePref = getSharedPreferences(SettingsActivity.THEME_PREFERENCE, MODE_PRIVATE);
        boolean isDarkMode = themePref.getBoolean(SettingsActivity.KEY_IS_NIGHT_MODE, false);

        if (isDarkMode){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        authorRv = findViewById(R.id.authorRv);
        backIb = findViewById(R.id.backIb);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        authorRv.setLayoutManager(layoutManager);

        authorDataList = new ArrayList<>();

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadAuthors();
    }

     private void loadAuthors() {

         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("author");
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 authorDataList.clear();
                 for (DataSnapshot ds: dataSnapshot.getChildren()){
                     AuthorData authorData = ds.getValue(AuthorData.class);

                     authorDataList.add(authorData);
                     authorAdapter = new AuthorAdapter(AuthorActivity.this, authorDataList);
                     authorRv.setAdapter(authorAdapter);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 Toast.makeText(AuthorActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });

     }
 }
