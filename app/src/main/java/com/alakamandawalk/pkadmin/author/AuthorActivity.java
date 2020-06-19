 package com.alakamandawalk.pkadmin.author;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.SettingsActivity;
import com.alakamandawalk.pkadmin.model.AuthorData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

 public class AuthorActivity extends AppCompatActivity {

    RecyclerView authorRv;
    ImageButton backIb;

    AuthorAdapter authorAdapter;
    List<AuthorData> authorDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        Configuration configuration = new Configuration();
        setLocale(configuration);

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

     @Override
     public void applyOverrideConfiguration(Configuration overrideConfiguration) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1){
             setLocale(overrideConfiguration);
             applyOverrideConfiguration(overrideConfiguration);
         }
     }

     public void setLocale(Configuration config) {

         SharedPreferences languagePreference = getSharedPreferences(SettingsActivity.LANGUAGE_PREF, Context.MODE_PRIVATE);
         String lang =  languagePreference.getString(SettingsActivity.LANGUAGE_KEY, SettingsActivity.ENGLISH);
         String language;
         if (lang.equals(SettingsActivity.SINHALA)){

             language = SettingsActivity.SINHALA;
         }else {
             language = SettingsActivity.ENGLISH;
         }

         Locale locale = new Locale(language);
         Locale.setDefault(locale);
         if (Build.VERSION.SDK_INT>=17){
             config.setLocale(locale);
         } else {
             config.locale = locale;
         }
         getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
     }
 }
